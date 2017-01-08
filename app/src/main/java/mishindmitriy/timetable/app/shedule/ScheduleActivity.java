package mishindmitriy.timetable.app.shedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.provider.Settings;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.nshmura.recyclertablayout.RecyclerTabLayout;

import org.joda.time.LocalDate;

import java.util.List;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseAdapter;
import mishindmitriy.timetable.app.schedulesubjects.ScheduleSubjectAdapter;
import mishindmitriy.timetable.app.schedulesubjects.ScheduleSubjectsActivity;
import mishindmitriy.timetable.model.ScheduleSubject;
import rx.Observable;

public class ScheduleActivity extends MvpAppCompatActivity implements ScheduleView, FeedbackView {
    public final static int PAGES_COUNT = 100;
    protected TextView currentThingTitle;
    protected Toolbar toolbar;
    protected RecyclerTabLayout tabLayout;
    protected ViewPager viewPager;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected TextView chooseThingText;
    protected RecyclerView recyclerView;
    protected NavigationView nvView;
    protected DrawerLayout mDrawerLayout;
    @InjectPresenter
    SchedulePresenter schedulePresenter;
    @InjectPresenter
    FeedbackPresenter feedbackPresenter;
    private ActionBarDrawerToggle mDrawerToggle;
    private ScheduleSubjectAdapter scheduleSubjectAdapter = new ScheduleSubjectAdapter(Observable.just(""));
    private DatePickerDialog dateDialog;
    private DaysPagerAdapter pagerAdapter;
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            if (dateDialog != null) {
                dateDialog.dismiss();
                dateDialog = null;
            }
            LocalDate newDate = new LocalDate(
                    String.format("%d-%d-%d",
                            year,
                            month + 1,
                            dayOfMonth)
            );
            schedulePresenter.onDateSelected(newDate);
        }
    };
    private AlertDialog feedbackDialog;
    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_shedule);
        initView();
        pagerAdapter = new DaysPagerAdapter(schedulePresenter.getRealm());
        init();
        chooseThingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        startActivity(new Intent(ScheduleActivity.this, ScheduleSubjectsActivity.class));
                        mDrawerLayout.removeDrawerListener(this);
                    }
                });
                mDrawerLayout.closeDrawers();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (dateDialog != null) dateDialog.dismiss();
        if (feedbackDialog != null) feedbackDialog.dismiss();
        if (loadingDialog != null) loadingDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void showCurrentSubjectTitle(String name) {
        currentThingTitle.setText(name);
    }

    @Override
    public void setSubjectsData(List<ScheduleSubject> scheduleSubjects) {
        scheduleSubjectAdapter.setData(scheduleSubjects);
    }

    protected void init() {
        chooseThingText.setText(R.string.choose_thing);

        if (!schedulePresenter.isSubjectSelected() || !schedulePresenter.isSubjectNotNull()) {
            startActivity(new Intent(this, ScheduleSubjectsActivity.class));
            finish();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(scheduleSubjectAdapter);

        scheduleSubjectAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<ScheduleSubject>() {
            @Override
            public void onItemClick(final ScheduleSubject scheduleSubject) {
                schedulePresenter.scheduleSubjectClicked(scheduleSubject);
                mDrawerLayout.closeDrawers();
            }
        });

        toolbar.inflateMenu(R.menu.menu_shedule);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.case_date:
                        if (dateDialog == null) {
                            dateDialog = new DatePickerDialog(ScheduleActivity.this,
                                    onDateSetListener,
                                    schedulePresenter.getStartDate().getYear(),
                                    schedulePresenter.getStartDate().getMonthOfYear() - 1,
                                    schedulePresenter.getStartDate().getDayOfMonth());
                        }
                        dateDialog.setCancelable(true);
                        dateDialog.show();
                        break;
                    case R.id.feedback:
                        showFeedbackAlert();
                        break;
                }
                return false;
            }
        });

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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                schedulePresenter.refreshData();
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

    @Override
    public void setStartDate(LocalDate newDate) {
        pagerAdapter.setStartDate(newDate);
    }

    @Override
    public void notifyPagerDateChanged() {
        pagerAdapter.notifyDataChanged();
        updateTabs();
        viewPager.setCurrentItem(PAGES_COUNT / 2, true);
        tabLayout.scrollToPosition(PAGES_COUNT / 2);
    }

    @Override
    public void dismissFeedbackAlert() {
        if (feedbackDialog != null && feedbackDialog.isShowing()) {
            feedbackDialog.dismiss();
            feedbackDialog = null;
        }
    }

    @Override
    public void showLoadingAlert(boolean show) {
        if (show) {
            if (loadingDialog != null && loadingDialog.isShowing()) return;
            loadingDialog = new ProgressDialog.Builder(this)
                    .setTitle(R.string.loading)
                    .create();
            loadingDialog.show();
        } else {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }
    }

    @Override
    public void showFeedbackAlert() {
        View view = getLayoutInflater().inflate(R.layout.alert_feedback, null);
        final EditText editText = (EditText) view.findViewById(R.id.text);
        feedbackDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        feedbackPresenter.dismissFeedbackAlert();
                        String androidId = Settings.Secure.getString(
                                getContentResolver(),
                                Settings.Secure.ANDROID_ID
                        );
                        feedbackPresenter.sendFeedback(androidId, editText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        feedbackPresenter.dismissFeedbackAlert();
                    }
                })
                .create();
        feedbackDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTabs();
        schedulePresenter.loadIfNeed();
        feedbackPresenter.checkNeedFeedbackAlert();
    }

    @Override
    public void setRefreshing(final boolean enable) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(enable);
                }
            });
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

    private void initView() {
        currentThingTitle = (TextView) findViewById(R.id.current_thing_title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (RecyclerTabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        chooseThingText = (TextView) findViewById(R.id.choose_thing);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        nvView = (NavigationView) findViewById(R.id.nvView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.sheduleLayout);
    }
}
