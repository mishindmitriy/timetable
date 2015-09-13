package mishindmitriy.timetable;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.support.design.widget.Snackbar;

import mishindmitriy.timetable.TolgasModel.DayPairs;
import mishindmitriy.timetable.TolgasModel.SheduleActivityModel;
import mishindmitriy.timetable.TolgasModel.SheduleListAdapter;
import mishindmitriy.timetable.TolgasModel.TolgasModel;

public class SheduleActivity extends AppCompatActivity
        implements SheduleActivityModel.Observer {

    private CharSequence mTitle;
    private SwipeRefreshLayout mSwipeLayout;
    private SheduleActivityModel mSheduleModel;
    private boolean isCreate = true;
    private final static String TAG = "SheduleActivity";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private SheduleListAdapter mSheduleAdapter;
    private ListView mListShedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(null);
        Log.d(TAG, "startOnCreate");

        mSheduleModel = new SheduleActivityModel(getSharedPreferences(String.valueOf(PreferensesConst.APP_PREFERENCES), Context.MODE_PRIVATE), getCacheDir().getPath());

        //чтение настроек из файла
        //если в настройках нет записи, то запускаем активность со списком групп
        if (mSheduleModel.isGroupAvailable()) {
            finish();
            Intent intent = new Intent(this, CaseGroupActivity.class);
            startActivity(intent);
        } else {
            setTitle(mSheduleModel.getGroupName());
        }
        mSheduleModel.registerObserver(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shedule);
        mListShedule = (ListView) findViewById(R.id.dayPairsLayoutInActivity);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.sheduleSwipeRefresh);
        mSwipeLayout.setSoundEffectsEnabled(true);
        mSwipeLayout.setColorSchemeResources(R.color.teal500);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onSwipeRefresh");
                mSwipeLayout.playSoundEffect(SoundEffectConstants.CLICK);
                mSheduleModel.LoadData();
            }
        });

        mTitle = mDrawerTitle = getTitle();
        String[] mScreenTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.sheduleLayout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mScreenTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Initialize the first fragment when the application first loads.
        if (savedInstanceState == null) {
            selectItem(0);
        }

        Log.d(TAG, "finishOnCreate");
        mSheduleAdapter=new SheduleListAdapter(SheduleActivity.this,mSheduleModel.getShedule());
        mListShedule.setAdapter(mSheduleAdapter);
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.refresh_icon).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        Log.d(TAG, "onSectionAttached pos="+position);
        if (isCreate) {
            Log.i(TAG, "isCreate=false");
            isCreate = false;
            position = mSheduleModel.getPeriod();
        }
        if (mSheduleModel.isWorking()) mSheduleModel.StopLoad();
        switch (position) {
            case TolgasModel.TODAY://сегодня
                mSheduleModel.setPeriod(TolgasModel.TODAY);
                break;
            case TolgasModel.TOMORROW://Завтра
                mSheduleModel.setPeriod(TolgasModel.TOMORROW);
                break;
            case TolgasModel.SEVEN_DAYS://7 дней
                mSheduleModel.setPeriod(TolgasModel.SEVEN_DAYS);
                break;
            case TolgasModel.THIS_WEEK:
                mSheduleModel.setPeriod(TolgasModel.THIS_WEEK);
                break;
            case TolgasModel.NEXT_WEEK:
                mSheduleModel.setPeriod(TolgasModel.NEXT_WEEK);
                break;
            case TolgasModel.THIS_MONTH:
                mSheduleModel.setPeriod(TolgasModel.THIS_MONTH);
                break;
            case TolgasModel.NEXT_MONTH:
                mSheduleModel.setPeriod(TolgasModel.NEXT_MONTH);
                break;
            case TolgasModel.CASE_GROUP://Выбрать группу
                finish();
                Intent intent = new Intent(this, CaseGroupActivity.class);
                startActivity(intent);
                return;
        }
        mTitle = mSheduleModel.getGroupName();
        mSheduleModel.LoadData();
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        if (getSupportActionBar()!=null) getSupportActionBar().setTitle(mTitle);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shedule, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoadStarted(SheduleActivityModel sheduleActivityModel) {
        Log.i(TAG, "OnLoadStart");
        if (mSwipeLayout != null) {
            Log.i(TAG, "piu runnable will be run");
            mSwipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeLayout.setRefreshing(true);
                    Log.i(TAG, "piu refresh true");
                }
            });
        }
        mListShedule=(ListView)findViewById(R.id.dayPairsLayoutInActivity);
        mListShedule.smoothScrollToPosition(0);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        if (scrollView != null) scrollView.fullScroll(View.FOCUS_UP);
    }

    @Override
    public void onLoadFinished(SheduleActivityModel sheduleActivityModel) {
        Log.i(TAG, "OnLoadFinished");
        List<DayPairs> shedule = sheduleActivityModel.getShedule();
        mSheduleAdapter.setData(shedule);
        mSwipeLayout.setRefreshing(false);
        Log.i(TAG, "refresh false");
        //congratulations(shedule.size());
        Snackbar.make(mSwipeLayout, "Расписание обновлено", 2000).show();
    }

    private void congratulations(int size) {
        if (size == 0) {
            //LinearLayout linearLayout = (LinearLayout) findViewById(R.id.dayPairsLayoutOnActivity);
            TextView congratulations = (TextView) getLayoutInflater().inflate(R.layout.congratulations, null);
            //linearLayout.addView(congratulations);
        }
    }

    @Override
    public void onLoadFailed(SheduleActivityModel sheduleActivityModel) {
        Log.i(TAG, "OnLoadFailed");
        List<DayPairs> shedule = sheduleActivityModel.getShedule();
        mSheduleAdapter.setData(shedule);
        mSwipeLayout.setRefreshing(false);
        Snackbar.make(mSwipeLayout, "Ошибка загрузки. Загружены локальные данные.", 2000).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    //Вызывается при нажатии кнопки меню
    {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle presses on the action bar items
        switch (id) {
            case R.id.refresh_icon:
                if (!mSheduleModel.isWorking()) mSheduleModel.LoadData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) mSheduleModel.StopLoad();

        mSheduleModel.unregisterObserver(this);

        SharedPreferences preferences = getSharedPreferences(String.valueOf(PreferensesConst.APP_PREFERENCES), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Log.i(TAG, "save period " + mSheduleModel.getPeriod());
        editor.putInt(String.valueOf(PreferensesConst.PERIOD), mSheduleModel.getPeriod());
        editor.apply();
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

}
