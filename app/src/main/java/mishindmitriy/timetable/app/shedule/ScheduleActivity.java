package mishindmitriy.timetable.app.shedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.github.mishindmitriy.feedbackhelper.FeedbackAlertHelper;

import org.joda.time.LocalDate;

import java.util.List;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseAdapter;
import mishindmitriy.timetable.app.schedulesubjects.ScheduleSubjectAdapter;
import mishindmitriy.timetable.app.schedulesubjects.ScheduleSubjectsActivity;
import mishindmitriy.timetable.databinding.ActivityScheduleBinding;
import mishindmitriy.timetable.model.ScheduleSubject;
import mishindmitriy.timetable.utils.FirebaseHelper;

public class ScheduleActivity extends MvpAppCompatActivity implements ScheduleView {
    public final static int PAGES_COUNT = 100;
    @InjectPresenter
    SchedulePresenter schedulePresenter;
    private ActionBarDrawerToggle mDrawerToggle;
    private ScheduleSubjectAdapter scheduleSubjectAdapter = new ScheduleSubjectAdapter();
    private DatePickerDialog dateDialog;
    private DaysPagerAdapter pagerAdapter;
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            LocalDate newDate = new LocalDate(
                    String.format("%d-%d-%d",
                            year,
                            month + 1,
                            dayOfMonth)
            );
            schedulePresenter.onDateSelected(newDate);
            schedulePresenter.dismissDateDialog();
        }
    };
    private AlertDialog feedbackDialog;
    private ActivityScheduleBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule);
        pagerAdapter = new DaysPagerAdapter(schedulePresenter.getRealm());
        init();
        binding.navigation.chooseThing.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        startActivity(new Intent(ScheduleActivity.this, ScheduleSubjectsActivity.class));
                        binding.drawerLayout.removeDrawerListener(this);
                    }
                });
                binding.drawerLayout.closeDrawers();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (dateDialog != null) dateDialog.dismiss();
        if (feedbackDialog != null) feedbackDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void dismissDateDialog() {
        if (dateDialog != null) {
            dateDialog.dismiss();
            dateDialog = null;
        }
    }

    @Override
    public void showFeedbackAlert() {
        new FeedbackAlertHelper.Builder()
                .from(this)
                .setFeedbackListener(new FeedbackAlertHelper.FeedbackListener() {
                    @Override
                    public void onFeedbackSubmit(String feedback) {
                        FirebaseHelper.sendFeedback(getContentResolver(), feedback);
                    }
                })
                .build()
                .showFeedbackAlert();
    }

    @Override
    public void showCurrentSubjectTitle(String name) {
        binding.currentThingTitle.setText(name);
    }

    @Override
    public void setSubjectsData(List<ScheduleSubject> scheduleSubjects) {
        scheduleSubjectAdapter.setData(scheduleSubjects);
    }

    @Override
    public void showDateDialog() {
        if (dateDialog == null) {
            dateDialog = new DatePickerDialog(
                    this,
                    onDateSetListener,
                    schedulePresenter.getStartDate().getYear(),
                    schedulePresenter.getStartDate().getMonthOfYear() - 1,
                    schedulePresenter.getStartDate().getDayOfMonth());
        }
        dateDialog.setCancelable(true);
        dateDialog.show();
    }

    protected void init() {
        binding.navigation.chooseThing.text.setText(R.string.choose_thing);

        if (!schedulePresenter.isSubjectSelected()) {
            startActivity(new Intent(this, ScheduleSubjectsActivity.class));
            finish();
            return;
        }

        binding.navigation.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.navigation.recyclerView.setAdapter(scheduleSubjectAdapter);

        scheduleSubjectAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<ScheduleSubject>() {
            @Override
            public void onItemClick(final ScheduleSubject scheduleSubject) {
                schedulePresenter.scheduleSubjectClicked(scheduleSubject);
                binding.drawerLayout.closeDrawers();
            }
        });

        binding.toolbar.inflateMenu(R.menu.menu_shedule);
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.case_date:
                        schedulePresenter.showDateDialog();
                        break;
                    case R.id.feedback:
                        schedulePresenter.showFeedbackAlert();
                        break;
                }
                return false;
            }
        });

        {
            // setScheduleSubject navigation drawer
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                    /* host Activity */
                    ScheduleActivity.this.binding.drawerLayout, binding.toolbar,                    /* DrawerLayout object */
                    R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                    R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
            );
            // Set the drawer toggle as the DrawerListener
            binding.drawerLayout.setDrawerListener(mDrawerToggle);
        }

        binding.viewPager.setAdapter(pagerAdapter);
        binding.tabLayout.setUpWithViewPager(binding.viewPager);

        binding.viewPager.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                binding.tabLayout.getAdapter().notifyDataSetChanged();
            }
        });

        binding.viewPager.setCurrentItem(PAGES_COUNT / 2, false);

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                schedulePresenter.refreshData();
            }
        });

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                        if (!binding.swipeRefreshLayout.isRefreshing()) {
                            binding.swipeRefreshLayout.setEnabled(false);
                        }
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                    case ViewPager.SCROLL_STATE_IDLE:
                        binding.swipeRefreshLayout.setEnabled(true);
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
        binding.viewPager.setCurrentItem(PAGES_COUNT / 2, true);
        binding.tabLayout.scrollToPosition(PAGES_COUNT / 2);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTabs();
        schedulePresenter.loadIfNeed();
    }

    @Override
    public void setRefreshing(final boolean enable) {
        if (binding.swipeRefreshLayout != null) {
            binding.swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    binding.swipeRefreshLayout.setRefreshing(enable);
                }
            });
        }
    }

    private void updateTabs() {
        binding.tabLayout.getAdapter().notifyDataSetChanged();
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
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
