package mishindmitriy.timetable.app.shedule;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;

import com.nshmura.recyclertablayout.RecyclerTabLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import io.realm.Realm;
import io.realm.Sort;
import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseActivity;
import mishindmitriy.timetable.app.base.BaseAdapter;
import mishindmitriy.timetable.app.things.ThingAdapter;
import mishindmitriy.timetable.app.things.ThingsActivity_;
import mishindmitriy.timetable.model.Thing;
import mishindmitriy.timetable.utils.DataHelper;
import mishindmitriy.timetable.utils.Prefs;

@EActivity(R.layout.activity_shedule)
public class SheduleActivity extends BaseActivity {
    private final int pagesCount = 100;
    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;
    @ViewById(R.id.sheduleLayout)
    protected DrawerLayout mDrawerLayout;
    @ViewById(R.id.current_thing_title)
    protected TextView currentThingTextView;
    @ViewById(R.id.nvView)
    protected NavigationView navigationView;
    @ViewById(R.id.recyclerView)
    protected RecyclerView recyclerView;
    @ViewById(R.id.viewPager)
    protected ViewPager viewPager;
    @ViewById(R.id.tabLayout)
    protected RecyclerTabLayout tabLayout;
    @ViewById(R.id.choose_thing)
    protected TextView chooseThingText;
    @InstanceState
    protected DateTime lastUpdate;
    @InstanceState
    protected LocalDate startDate = LocalDate.now();
    private SharedPreferences.OnSharedPreferenceChangeListener listener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Prefs.KEY_SELECTED_THING_ID)) {
                refreshData();
                Thing currentThing = realm.where(Thing.class)
                        .equalTo("id", Prefs.get().getSelectedThingId())
                        .findFirst();
                if (currentThing != null) {
                    currentThingTextView.setText(currentThing.getName());
                }
            }
        }
    };
    private ActionBarDrawerToggle mDrawerToggle;
    private ThingAdapter thingAdapter = new ThingAdapter();
    private DatePickerDialog dialog;

    @Click(R.id.choose_thing)
    protected void chooseThingClicked() {
        ThingsActivity_.intent(SheduleActivity.this).start();
    }

    private boolean canUpdate() {
        final long HOUR = 3600000;
        return lastUpdate == null
                || (DateTime.now().getMillis() - lastUpdate.getMillis()) > HOUR;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Prefs.get().register(listener);
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) dialog.dismiss();
        super.onDestroy();
        Prefs.get().unregister(listener);
    }

    @AfterViews
    protected void init() {
        chooseThingText.setText(R.string.choose_thing);

        Thing currentThing = realm.where(Thing.class)
                .equalTo("id", Prefs.get().getSelectedThingId())
                .findFirst();

        if (Prefs.get().getSelectedThingId() == 0
                || currentThing == null) {
            ThingsActivity_.intent(this).start();
            finish();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(thingAdapter);
        thingAdapter.setData(realm.where(Thing.class)
                .greaterThan("timesOpen", 0)
                .findAllSortedAsync("timesOpen", Sort.DESCENDING, "name", Sort.ASCENDING));
        thingAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<Thing>() {
            @Override
            public void onItemClick(Thing thing) {
                if (thing.getId() != Prefs.get().getSelectedThingId()) {
                    setNewThing(thing);
                }
                mDrawerLayout.closeDrawers();
            }
        });

        toolbar.inflateMenu(R.menu.menu_shedule);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.case_date:
                        if (dialog == null) {
                            dialog = new DatePickerDialog(SheduleActivity.this,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                            if (dialog != null) {
                                                dialog.dismiss();
                                                dialog = null;
                                            }
                                            LocalDate newDate = new LocalDate(
                                                    String.format("%d-%d-%d",
                                                            year,
                                                            month + 1,
                                                            dayOfMonth)
                                            );
                                            onDateSelected(newDate);
                                        }
                                    },
                                    startDate.getYear(),
                                    startDate.getMonthOfYear() - 1,
                                    startDate.getDayOfMonth());
                        }
                        dialog.setCancelable(true);
                        dialog.show();
                        break;
                }
                return false;
            }
        });


        currentThingTextView.setText(currentThing.getName());

        DataHelper.loadSchedule(null, startDate);

        {
            // setThing navigation drawer
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                    /* host Activity */
                    SheduleActivity.this.mDrawerLayout, toolbar,                    /* DrawerLayout object */
                    R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                    R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
            );
            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }

        initPager();
    }

    private void onDateSelected(LocalDate newDate) {
        if (!startDate.isEqual(newDate)) {
            startDate = newDate;
            DataHelper.loadSchedule(null, startDate);
            viewPager.getAdapter().notifyDataSetChanged();
            lastUpdate = DateTime.now();
        }
        viewPager.setCurrentItem(pagesCount / 2, true);
    }

    private void setNewThing(final Thing thing) {
        if (thing == null) return;
        Prefs.get().setSelectedThingId(thing.getId());
        final Long id = thing.getId();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Thing currentThing = realm.where(Thing.class)
                        .equalTo("id", id)
                        .findFirst();
                currentThing.incrementOpenTimes();
            }
        });
    }

    private void initPager() {
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            private LocalDate getLocalDate(int pos) {
                LocalDate date = startDate.plusDays(pos - pagesCount / 2);
                Log.d("testtt", "startdate " + startDate.toString() + "; " + pos + " pos date " + date.toString());
                return date;
            }

            @Override
            public int getItemPosition(Object object) {
                // TODO: 02.10.2016 fragments just restarted, not creating new
                return POSITION_NONE;
            }

            @Override
            public Fragment getItem(int position) {
                return DayPairsFragment_.builder()
                        .localDate(getLocalDate(position))
                        .build();
            }

            @Override
            public int getCount() {
                return pagesCount;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                LocalDate localDate = getLocalDate(position);
                if (localDate.isEqual(LocalDate.now())) {
                    return "Сегодня";
                } else if (localDate.isEqual(LocalDate.now().minusDays(1))) {
                    return "Вчера";
                } else if (localDate.isEqual(LocalDate.now().plusDays(1))) {
                    return "Завтра";
                } else {
                    return localDate.toString("dd MMM EE");
                }
            }
        });

        tabLayout.setUpWithViewPager(viewPager);

        viewPager.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                tabLayout.getAdapter().notifyDataSetChanged();
            }
        });

        viewPager.setCurrentItem(pagesCount / 2, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTabs();
        refreshData();
    }

    private void refreshData() {
        if (canUpdate()) {
            DataHelper.loadSchedule(null, startDate);
            lastUpdate = DateTime.now();
        }
    }

    private void updateTabs() {
        tabLayout.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (this.mDrawerToggle != null) mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        if (this.mDrawerToggle != null) mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
