package mishindmitriy.timetable.app.shedule;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.Sort;
import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseActivity;
import mishindmitriy.timetable.app.base.BaseAdapter;
import mishindmitriy.timetable.app.notifications.NotificationService;
import mishindmitriy.timetable.app.schedulesubjects.ScheduleSubjectAdapter;
import mishindmitriy.timetable.app.schedulesubjects.ScheduleSubjectsActivity_;
import mishindmitriy.timetable.model.Pair;
import mishindmitriy.timetable.model.ScheduleSubject;
import mishindmitriy.timetable.utils.DataHelper;
import mishindmitriy.timetable.utils.Prefs;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_shedule)
public class ScheduleActivity extends BaseActivity {
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
    @ViewById(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @InstanceState
    protected DateTime lastUpdate;
    @InstanceState
    protected LocalDate startDate = LocalDate.now();
    private ActionBarDrawerToggle mDrawerToggle;
    private ScheduleSubjectAdapter scheduleSubjectAdapter = new ScheduleSubjectAdapter();
    private DatePickerDialog dialog;
    private DaysPagerAdapter pagerAdapter;
    private Observable<List<Pair>> dataUpdateObservable;
    private Subscription dataUpdateSubscription;
    private SharedPreferences.OnSharedPreferenceChangeListener listener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Prefs.KEY_SELECTED_THING_ID)) {
                lastUpdate = null;
                onDateSelected(LocalDate.now());
                ScheduleSubject currentScheduleSubject = realm.where(ScheduleSubject.class)
                        .equalTo("id", prefs.getSelectedThingId())
                        .findFirst();
                if (currentScheduleSubject != null) {
                    currentThingTextView.setText(currentScheduleSubject.getName());
                }
            }
        }
    };
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
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
    };

    @Click(R.id.choose_thing)
    protected void chooseThingClicked() {
        ScheduleSubjectsActivity_.intent(ScheduleActivity.this).start();
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
        pagerAdapter = new DaysPagerAdapter(realm);
        prefs.register(listener);
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) dialog.dismiss();
        super.onDestroy();
        prefs.unregister(listener);
    }

    @AfterViews
    protected void init() {
        chooseThingText.setText(R.string.choose_thing);

        if (prefs.getSelectedThingId() == 0) {
            ScheduleSubjectsActivity_.intent(this).start();
            finish();
            return;
        }

        ScheduleSubject currentScheduleSubject = realm.where(ScheduleSubject.class)
                .equalTo("id", prefs.getSelectedThingId())
                .findFirst();

        if (currentScheduleSubject == null) {
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
                if (scheduleSubject.getId() != prefs.getSelectedThingId()) {
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
                            dialog = new DatePickerDialog(ScheduleActivity.this,
                                    onDateSetListener,
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

        {
            // setScheduleSubject navigation drawer
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                    /* host Activity */
                    ScheduleActivity.this.mDrawerLayout, toolbar,                    /* DrawerLayout object */
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

        dataUpdateObservable = createLoadPairsObservable(startDate);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        if (!swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setEnabled(false);
                        }
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                    case ViewPager.SCROLL_STATE_IDLE:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
            }
        });
    }

    public Observable<List<Pair>> createLoadPairsObservable(LocalDate startDate) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        final LocalDate from = startDate.minusDays(50);
        final LocalDate to = startDate.plusDays(50);
        return Observable.create(new Observable.OnSubscribe<List<Pair>>() {
            @Override
            public void call(Subscriber<? super List<Pair>> subscriber) {
                long id = prefs.getSelectedThingId();
                if (id == 0) throw new IllegalStateException();

                Realm realm = Realm.getDefaultInstance();
                ScheduleSubject scheduleSubject = null;
                try {
                    scheduleSubject = realm.where(ScheduleSubject.class)
                            .equalTo("id", id)
                            .findFirst();
                    if (scheduleSubject == null) throw new IllegalStateException();

                    List<Pair> pairs = DataHelper.getShedule(scheduleSubject, from, to);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(pairs);
                    }
                } catch (IOException e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                } finally {
                    realm.close();
                }
            }
        })
                .timeout(10, TimeUnit.SECONDS)
                .onErrorReturn(new Func1<Throwable, List<Pair>>() {
                    @Override
                    public List<Pair> call(Throwable throwable) {
                        return new ArrayList<Pair>();
                    }
                })
                .doOnNext(new Action1<List<Pair>>() {
                    @Override
                    public void call(final List<Pair> pairs) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                for (Pair p : pairs) {
                                    Pair existPair = realm.where(Pair.class)
                                            .equalTo("id", p.getId())
                                            .findFirst();
                                    if (existPair != null && existPair.isNotified()) {
                                        p.setNotified();
                                    }
                                }
                                realm.copyToRealmOrUpdate(pairs);
                                // TODO: 19.09.16 add remove old pairs for this period (change date type to millis?)
                            }
                        });
                        realm.close();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void onDateSelected(LocalDate newDate) {
        if (!startDate.isEqual(newDate)) {
            startDate = newDate;
            refreshData();
            pagerAdapter.setStartDate(newDate);
            lastUpdate = DateTime.now();
        }
        pagerAdapter.notifyDataChanged();
        updateTabs();
        viewPager.setCurrentItem(PAGES_COUNT / 2, false);
        Log.d("testtt", "date selected service");
        startService(new Intent(this, NotificationService.class));
    }

    private void setNewThing(final ScheduleSubject scheduleSubject) {
        if (scheduleSubject == null) return;
        prefs.setSelectedThingId(scheduleSubject.getId());
        final Long id = scheduleSubject.getId();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(ScheduleSubject.class)
                        .equalTo("id", id)
                        .findFirst()
                        .incrementOpenTimes();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d("testtt", "change subject service");
                startService(new Intent(ScheduleActivity.this, NotificationService.class));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTabs();
        if (canUpdate()) {
            refreshData();
        }
    }

    private void refreshData() {
        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (dataUpdateSubscription != null && !dataUpdateSubscription.isUnsubscribed()) {
            dataUpdateSubscription.unsubscribe();
        }
        dataUpdateSubscription = dataUpdateObservable.subscribe(new Subscriber<List<Pair>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                hideRefreshing();
            }

            @Override
            public void onNext(List<Pair> pairs) {
                lastUpdate = DateTime.now();
                hideRefreshing();
                if (pairs != null && pairs.size() > 0) {
                    Log.d("testtt", "onnext service");
                    startService(new Intent(ScheduleActivity.this, NotificationService.class));
                }
            }
        });
    }

    private void hideRefreshing() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
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
