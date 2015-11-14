package mishindmitriy.timetable.app.shedule;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.casething.CaseActivity_;
import mishindmitriy.timetable.model.SheduleListAdapter;
import mishindmitriy.timetable.model.SheduleModel;
import mishindmitriy.timetable.model.SheduleWorkerFragment;
import mishindmitriy.timetable.model.data.Pair;
import mishindmitriy.timetable.model.data.Thing;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

@EActivity(R.layout.activity_shedule)
@OptionsMenu(R.menu.menu_shedule)
public class SheduleActivity extends AppCompatActivity
        implements SheduleModel.Observer, AdapterView.OnItemSelectedListener {

    private static final String TAG = "SheduleActivity";
    private static final String TAG_WORKER = "SheduleModelWorkerTAG";

    @Extra
    protected Thing thing;

    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;
    @ViewById(R.id.sheduleSwipeRefresh)
    protected SwipeRefreshLayout mSwipeLayout;
    @ViewById(R.id.sheduleLayout)
    protected DrawerLayout mDrawerLayout;
    @ViewById(R.id.list_favorites_thing)
    protected ListView mDrawerList;
    @ViewById(R.id.dayPairsLayoutInActivity)
    protected StickyListHeadersListView mListShedule;
    @ViewById(R.id.title)
    protected TextView mViewTitle;
    @ViewById(R.id.spinner)
    protected Spinner spinner;
    @ViewById(R.id.scrollView)
    protected ScrollView scrollView;
    @ViewById(R.id.nvView)
    protected NavigationView navigationView;
    @FragmentByTag(TAG_WORKER)
    protected SheduleWorkerFragment sheduleWorkerFragment;
    private CharSequence mTitle;
    private SheduleModel mSheduleModel;
    private ActionBarDrawerToggle mDrawerToggle;
    private SheduleListAdapter mSheduleAdapter;

    @AfterViews
    protected void init() {
        this.getWindow().setBackgroundDrawable(null);

        {
            // setThing toolbar
            setSupportActionBar(toolbar);
            if (this.getSupportActionBar() != null) {
                this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                this.getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        {
            // setThing sheduleModel
            if (sheduleWorkerFragment != null)
                this.mSheduleModel = sheduleWorkerFragment.getSheduleModel();
            else {
                final SheduleWorkerFragment workerFragment = new SheduleWorkerFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(workerFragment, TAG_WORKER)
                        .commit();
                this.mSheduleModel = workerFragment.getSheduleModel();
                this.mSheduleModel.setThing(thing);
            }
            this.mSheduleModel.registerObserver(this);

            if (mSheduleModel.isThingAvailable()) {
                this.mTitle = this.mSheduleModel.getThingName();
            } else {
                this.finish();
                CaseActivity_.intent(this).start();
                finish();
                return;
            }
        }

        mViewTitle.setText(mTitle);

        {
            this.mSwipeLayout.setSoundEffectsEnabled(true);
            this.mSwipeLayout.setColorSchemeResources(R.color.teal500);
            this.mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    SheduleActivity.this.mSwipeLayout.playSoundEffect(SoundEffectConstants.CLICK);
                    if (mSheduleModel != null) SheduleActivity.this.mSheduleModel.LoadData();
                }
            });
        }

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

        {
            // setThing spinner
            spinner.setAdapter(ArrayAdapter.createFromResource(this, R.array.menu_array, R.layout.spinner_item));
            spinner.setOnItemSelectedListener(this);
            spinner.setSelection(mSheduleModel.getPeriodPosition());
        }

        mSheduleAdapter = new SheduleListAdapter(this, mSheduleModel.getShedule(), mSheduleModel.getWhatThing());
        mListShedule.setAdapter(mSheduleAdapter);

        if (mSheduleModel.isWorking()) onLoadStarted();
        else mSheduleModel.LoadData();
    }

    @Click(R.id.case_button)
    void caseClick()
    {
        CaseActivity_.intent(this).start();
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (this.mDrawerToggle != null) this.mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        if (this.mDrawerToggle != null) this.mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public final void onLoadStarted() {
        this.mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                SheduleActivity.this.mSwipeLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onLoadFinished(List<Pair> shedule,boolean isCache) {
        if (mSheduleModel.getPeriodPosition()>0) mSheduleAdapter.setSetToday(true);
        else mSheduleAdapter.setSetToday(false);
        this.mSheduleAdapter.setData(shedule, this.mSheduleModel.getWhatThing());
        this.mSwipeLayout.setRefreshing(false);
        if (isCache) Snackbar.make(this.mSwipeLayout, "Ошибка загрузки. Отображены локальные данные.", Snackbar.LENGTH_LONG).show();
        this.mListShedule.setSelection(0);
    }

    @OptionsItem(R.id.refresh_icon)
    void refreshIconClick()
    {
        if (this.mSheduleModel != null) this.mSheduleModel.LoadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mSheduleModel == null) return;
        if (this.isFinishing()) {
            this.mSheduleModel.StopLoad();
        }
        this.mSheduleModel.unregisterObserver(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSheduleModel.setPeriod(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
