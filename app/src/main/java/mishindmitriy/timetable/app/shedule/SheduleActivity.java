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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.List;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.casething.CaseActivity_;
import mishindmitriy.timetable.model.SheduleListAdapter;
import mishindmitriy.timetable.model.SheduleModel;
import mishindmitriy.timetable.model.SheduleWorkerFragment;
import mishindmitriy.timetable.model.data.Pair;
import mishindmitriy.timetable.model.data.PeriodType;
import mishindmitriy.timetable.model.data.Thing;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

@EActivity(R.layout.activity_shedule)
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
    @ViewById(R.id.left_drawer)
    protected ListView mDrawerList;
    @ViewById(R.id.dayPairsLayoutInActivity)
    protected StickyListHeadersListView mListShedule;
    //@ViewById(R.id.title)
    //protected TextView mViewTitle;
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
            // init toolbar
            setSupportActionBar(toolbar);
            if (this.getSupportActionBar() != null) {
                this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                this.getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        {
            // init sheduleModel
            if (sheduleWorkerFragment != null)
                this.mSheduleModel = sheduleWorkerFragment.getSheduleModel();
            else {
                final SheduleWorkerFragment workerFragment = new SheduleWorkerFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(workerFragment, TAG_WORKER)
                        .commit();
                this.mSheduleModel = workerFragment.getSheduleModel();
                final File cacheDir = this.getCacheDir();
                final String path = cacheDir.getPath();
                this.mSheduleModel.init(path, thing);
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

        //mViewTitle.setText(mTitle);

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
            // init navigation drawer
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                    /* host Activity */
                    SheduleActivity.this.mDrawerLayout, toolbar,                    /* DrawerLayout object */
                    R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                    R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
            );
            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            setupDrawerContent(navigationView);
        }

        {
            // init spinner
            spinner.setAdapter(ArrayAdapter.createFromResource(this, R.array.menu_array, R.layout.spinner_item));
            spinner.setOnItemSelectedListener(this);
            spinner.setSelection(mSheduleModel.getPeriodPosition());
        }

        mSheduleAdapter = new SheduleListAdapter(this, mSheduleModel.getShedule(), mSheduleModel.getWhatThing());
        mListShedule.setAdapter(mSheduleAdapter);

        if (mSheduleModel.isWorking()) onLoadStarted(mSheduleModel);
        else mSheduleModel.LoadData();
    }

    private void selectItem(int position) {
        if (mSheduleModel.isWorking()) mSheduleModel.StopLoad();
        switch (position) {

        }
        mTitle = mSheduleModel.getThingName();
        mSheduleModel.LoadData();
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(mTitle);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(final MenuItem menuItem) {
        PeriodType period = this.mSheduleModel.getPeriod();
        int size = this.mSheduleModel.getShedule().size();
        //TODO сделать проверку, если нажато то, что уже нажато то ничо не делать
        if (this.mSheduleModel.isWorking()) this.mSheduleModel.StopLoad();
        switch (menuItem.getItemId()) {
            case R.id.item_today://сегодня
                //this.mSheduleModel.setPeriod(TolgasParseModel.TODAY);
                break;
            /*case id.item_tomorrow://Завтра
                this.mSheduleModel.setPeriod(TolgasParseModel.TOMORROW);
                break;
            case id.item_seven://7 дней
                this.mSheduleModel.setPeriod(TolgasParseModel.SEVEN_DAYS);
                break;
            case id.item_curr_week:
                this.mSheduleModel.setPeriod(TolgasParseModel.THIS_WEEK);
                break;
            case id.item_next_week:
                this.mSheduleModel.setPeriod(TolgasParseModel.NEXT_WEEK);
                break;
            case id.item_curr_month:
                this.mSheduleModel.setPeriod(TolgasParseModel.THIS_MONTH);
                break;
            case id.item_next_month:
                this.mSheduleModel.setPeriod(TolgasParseModel.NEXT_MONTH);
                break;*/
            case R.id.item_caseThing://Выбрать группу
                //this.finish();
                CaseActivity_.intent(this).start();
                mDrawerLayout.closeDrawers();
                return;
            default:
                return;
        }
        this.mTitle = this.mSheduleModel.getThingName();
        //mViewTitle.setText(mTitle);
        this.mSheduleModel.LoadData();
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menuSheduleActiviry) {
        // Inflate the menu;
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_shedule, menuSheduleActiviry);
        return super.onCreateOptionsMenu(menuSheduleActiviry);
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
    public final void onLoadStarted(final SheduleModel sheduleModel) {
        this.mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                SheduleActivity.this.mSwipeLayout.setRefreshing(true);
            }
        });

//        this.mListShedule.smoothScrollToPosition(0);
        //scrollView.fullScroll(View.FOCUS_UP);
    }

    @Override
    public void onLoadFinished(final SheduleModel sheduleModel) {
        List<Pair> shedule = sheduleModel.getShedule();
        this.mSheduleAdapter.setData(shedule, this.mSheduleModel.getWhatThing());
        this.mSwipeLayout.setRefreshing(false);
        Snackbar.make(this.mSwipeLayout, "Расписание обновлено", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public final void onLoadFailed(final SheduleModel sheduleModel) {
        List<Pair> shedule = sheduleModel.getShedule();
        this.mSheduleAdapter.setData(shedule, this.mSheduleModel.getWhatThing());
        this.mSwipeLayout.setRefreshing(false);
        Snackbar.make(this.mSwipeLayout, "Ошибка загрузки. Отображены локальные данные.", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    //Вызывается при нажатии кнопки меню
    {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        /*if (this.mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        // Handle presses on the action bar items
        switch (itemId) {
            case R.id.refresh_icon:
                if (this.mSheduleModel != null && !this.mSheduleModel.isWorking())
                    this.mSheduleModel.LoadData();
                return true;
            case android.R.id.home:
                if (this.mDrawerToggle.onOptionsItemSelected(item)) return true;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
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
        //if (position == mSheduleModel.getPeriod()) return; TODO
        if (this.mSheduleModel.isWorking()) this.mSheduleModel.StopLoad();
        switch (position) {
            case 0://сегодня
                this.mSheduleModel.setPeriod(PeriodType.TODAY);
                break;
            case 1://Завтра
                this.mSheduleModel.setPeriod(PeriodType.TOMORROW);
                break;
            case 2://7 дней
                this.mSheduleModel.setPeriod(PeriodType.SEVEN_DAYS);
                break;
            case 3:
                this.mSheduleModel.setPeriod(PeriodType.THIS_WEEK);
                break;
            case 4:
                this.mSheduleModel.setPeriod(PeriodType.NEXT_WEEK);
                break;
            case 5:
                this.mSheduleModel.setPeriod(PeriodType.THIS_MONTH);
                break;
            case 6:
                this.mSheduleModel.setPeriod(PeriodType.NEXT_MONTH);
                break;
            default:
                return;
        }
        this.mSheduleModel.LoadData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
