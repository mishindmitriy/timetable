package mishindmitriy.timetable.app.shedule;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import mishindmitriy.timetable.app.schedulesubjects.ScheduleSubjectAdapter;
import mishindmitriy.timetable.app.schedulesubjects.ScheduleSubjectsActivity_;
import mishindmitriy.timetable.model.ScheduleSubject;
import mishindmitriy.timetable.utils.DataHelper;
import mishindmitriy.timetable.utils.Prefs;

@EActivity(R.layout.activity_shedule)
public class SheduleActivity extends BaseActivity {
    public final static int PAGES_COUNT = 100;
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
    private ActionBarDrawerToggle mDrawerToggle;
    private ScheduleSubjectAdapter scheduleSubjectAdapter = new ScheduleSubjectAdapter();
    private DatePickerDialog dialog;
    private DaysPagerAdapter pagerAdapter = new DaysPagerAdapter(realm);
    private SharedPreferences.OnSharedPreferenceChangeListener listener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Prefs.KEY_SELECTED_THING_ID)) {
                lastUpdate = null;
                onDateSelected(LocalDate.now());
                ScheduleSubject currentScheduleSubject = realm.where(ScheduleSubject.class)
                        .equalTo("id", Prefs.get().getSelectedThingId())
                        .findFirst();
                if (currentScheduleSubject != null) {
                    currentThingTextView.setText(currentScheduleSubject.getName());
                }
            }
        }
    };

    @Click(R.id.choose_thing)
    protected void chooseThingClicked() {
        ScheduleSubjectsActivity_.intent(SheduleActivity.this).start();
        mDrawerLayout.closeDrawers();
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

        ScheduleSubject currentScheduleSubject = realm.where(ScheduleSubject.class)
                .equalTo("id", Prefs.get().getSelectedThingId())
                .findFirst();

        if (Prefs.get().getSelectedThingId() == 0
                || currentScheduleSubject == null) {
            ScheduleSubjectsActivity_.intent(this).start();
            finish();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(scheduleSubjectAdapter);
        scheduleSubjectAdapter.setData(realm.where(ScheduleSubject.class)
                .greaterThan("timesOpen", 0)
                .findAllSortedAsync("timesOpen", Sort.DESCENDING, "name", Sort.ASCENDING));
        scheduleSubjectAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<ScheduleSubject>() {
            @Override
            public void onItemClick(ScheduleSubject scheduleSubject) {
                if (scheduleSubject.getId() != Prefs.get().getSelectedThingId()) {
                    setNewThing(scheduleSubject);
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


        currentThingTextView.setText(currentScheduleSubject.getName());

        DataHelper.loadSchedule(null, startDate);

        {
            // setScheduleSubject navigation drawer
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                    /* host Activity */
                    SheduleActivity.this.mDrawerLayout, toolbar,                    /* DrawerLayout object */
                    R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                    R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
            );
            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setUpWithViewPager(viewPager);

        viewPager.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                tabLayout.getAdapter().notifyDataSetChanged();
            }
        });

        viewPager.setCurrentItem(PAGES_COUNT / 2, false);
    }

    private void onDateSelected(LocalDate newDate) {
        if (!startDate.isEqual(newDate)) {
            startDate = newDate;
            DataHelper.loadSchedule(null, startDate);
            pagerAdapter.setStartDate(newDate);
            lastUpdate = DateTime.now();
        }
        viewPager.setCurrentItem(PAGES_COUNT / 2, false);
    }

    private void setNewThing(final ScheduleSubject scheduleSubject) {
        if (scheduleSubject == null) return;
        Prefs.get().setSelectedThingId(scheduleSubject.getId());
        final Long id = scheduleSubject.getId();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ScheduleSubject currentScheduleSubject = realm.where(ScheduleSubject.class)
                        .equalTo("id", id)
                        .findFirst();
                currentScheduleSubject.incrementOpenTimes();
            }
        });
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
