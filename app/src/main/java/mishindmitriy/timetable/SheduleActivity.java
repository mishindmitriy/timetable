package mishindmitriy.timetable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mishindmitriy.timetable.TolgasModel.DayPairs;
import mishindmitriy.timetable.TolgasModel.SheduleActivityModel;
import mishindmitriy.timetable.TolgasModel.TolgasModel;

public class SheduleActivity extends AppCompatActivity
        implements //NavigationDrawerFragment.NavigationDrawerCallbacks,
        SheduleActivityModel.Observer {

    private CharSequence mTitle;
    private SwipeRefreshLayout mSwipeLayout;
    private SheduleActivityModel mSheduleModel;
    private boolean isCreate = true;
    private final static String TAG = "SheduleActivity";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "startOnCreate");

        mSheduleModel = new SheduleActivityModel(getSharedPreferences(String.valueOf(PreferensesConst.APP_PREFERENCES), Context.MODE_PRIVATE),getCacheDir().getPath());

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

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.sheduleSwipeRefresh);
        mSwipeLayout.setColorSchemeResources(R.color.teal500);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG,"onSwipeRefresh");
                mSheduleModel.LoadData();
            }
        });

//        mNavigationDrawerFragment = (NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
//        mTitle = getTitle();
//        // Set up the drawer.
//        mNavigationDrawerFragment.setUp(
//                R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.sheduleLayout));
//
//        Log.d(TAG, "finishOnCreate");
        mSheduleModel.loadFromCache();
    }

//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//        // update the main content by replacing fragments
//        Log.d(TAG, "onNavigationDrawerItemSelected");
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
//    }

    public void onSectionAttached(int number) {
        Log.d(TAG, "onSectionAttached");
        if (isCreate) {
            Log.i(TAG, "isCreate=false");
            isCreate = false;
            number = mSheduleModel.getPeriod() + 1;
        }
        if (mSheduleModel.isWorking()) mSheduleModel.StopLoad();
        switch (number) {
            case 1://сегодня
                mSheduleModel.setPeriod(TolgasModel.TODAY);
                mTitle = getString(R.string.TodayShedule);
                break;
            case 2://Завтра
                mSheduleModel.setPeriod(TolgasModel.TOMORROW);
                mTitle = getString(R.string.TomorowShedule);
                break;
            case 3://7 дней
                mSheduleModel.setPeriod(TolgasModel.SEVEN_DAYS);
                mTitle = getString(R.string.SevenDaysShedule);
                break;
            case 4://Выбрать группу
                finish();
                Intent intent = new Intent(this, CaseGroupActivity.class);
                startActivity(intent);
                return;
        }

        mSheduleModel.LoadData();
    }

    public void restoreActionBar() {
        Log.d(TAG, "restoreActionBar");
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setTitle(mSheduleModel.getGroupName());// + ", " + mTitle);
        //setTitle(mSheduleModel.getGroupName());
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onLoadStarted(SheduleActivityModel sheduleActivityModel) {
        Log.i(TAG,"OnLoadStart");
        if (mSwipeLayout != null) {
            Log.i(TAG, "piu runnable will be run");
            //mSwipeLayout.setRefreshing(true);
            mSwipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeLayout.setRefreshing(true);
                    Log.i(TAG, "piu refresh true");
                }
            });
        }
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        if (scrollView != null) scrollView.fullScroll(View.FOCUS_UP);
    }

    @Override
    public void onLoadFinished(SheduleActivityModel sheduleActivityModel) {
        Log.i(TAG,"OnLoadFinished");
        List<DayPairs> shedule = sheduleActivityModel.getShedule();
        if (shedule == null) {
            mSwipeLayout.setRefreshing(false);
            Log.i(TAG, "refresh false");
            Toast.makeText(SheduleActivity.this, "Загрузить данные не удалось. Проверьте интернет-соединение", Toast.LENGTH_LONG).show();
            return;
        }

        outputShedule(shedule);

        mSwipeLayout.setRefreshing(false);
        Log.i(TAG, "refresh false");
        congratulations(shedule.size());
    }

    private void congratulations(int size)
    {
        if (size == 0) {
            Toast.makeText(SheduleActivity.this, "Поздравляем! На выбраный период занятий нет", Toast.LENGTH_LONG).show();
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.dayPairsLayoutOnActivity);
            TextView congratulations = (TextView) getLayoutInflater().inflate(R.layout.congratulations, null);
            linearLayout.addView(congratulations);
        }
    }

    @Override
    public void onLoadFailed(SheduleActivityModel sheduleActivityModel) {
        Log.i(TAG,"OnLoadFailed");
        mSwipeLayout.setRefreshing(false);
        Toast.makeText(SheduleActivity.this, "Загрузить данные не удалось. Проверьте интернет-соединение", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoadCacheOk(SheduleActivityModel sheduleActivityModel) {
        Log.i(TAG,"OnLoadCacheOk");
        List<DayPairs> shedule = sheduleActivityModel.getShedule();
        outputShedule(shedule);
        congratulations(shedule.size());
    }

    private void outputShedule(List<DayPairs> output) {
        Log.i(TAG,"outputShedule");
        LinearLayout dayslayout = (LinearLayout) findViewById(R.id.dayPairsLayoutOnActivity);
        dayslayout.removeAllViews();
        LayoutInflater layoutInflater = getLayoutInflater();

        for (int i = 0; i < output.size(); i++) {
            LinearLayout dayLayout = (LinearLayout) layoutInflater.inflate(R.layout.day_pairs_layout, dayslayout, false);
            TextView viewDate = (TextView) dayLayout.findViewById(R.id.textViewDate);
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(TolgasModel.formatDate);
            try {
                date = sdf.parse(output.get(i).getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String dayOfWeek = TolgasModel.getDayOfWeek(date);

            viewDate.setText(sdf.format(date));// + ", " + dayOfWeek);
            TextView viewDayOfWeek = (TextView) dayLayout.findViewById(R.id.textViewDayOfWeek);
            viewDayOfWeek.setText(dayOfWeek);
            dayslayout.addView(dayLayout);

            for (int n = 0; n < output.get(i).getPairsArray().size(); n++) {
                RelativeLayout pairLayout = (RelativeLayout) layoutInflater.inflate(R.layout.pair, dayLayout, false);
                TextView viewClassroom = (TextView) pairLayout.findViewById(R.id.textViewClassroom);
                viewClassroom.setText(output.get(i).getPair(n).getClassroom());
                TextView viewSubject = (TextView) pairLayout.findViewById(R.id.textViewSubject);
                viewSubject.setText(output.get(i).getPair(n).getSubject());
                TextView viewPrepod = (TextView) pairLayout.findViewById(R.id.textViewPrepod);
                viewPrepod.setText(output.get(i).getPair(n).getPrepod());
                TextView viewTypePair = (TextView) pairLayout.findViewById(R.id.textViewTypePair);
                viewTypePair.setText(output.get(i).getPair(n).getTypePair());

                TextView viewPairStart = (TextView) pairLayout.findViewById(R.id.textViewPairStart);
                TextView viewPairEnd = (TextView) pairLayout.findViewById(R.id.textViewPairEnd);

                int d = Integer.parseInt(output.get(i).getPair(n).getPairNumber());
                if (dayOfWeek.equals("Суббота")) {   //время пар в субботу
                    switch (d) {
                        case 1:
                            viewPairStart.setText(TolgasModel.Saturday.firstPairStart);
                            viewPairEnd.setText(TolgasModel.Saturday.firstPairEnd);
                            break;
                        case 2:
                            viewPairStart.setText(TolgasModel.Saturday.secondPairStart);
                            viewPairEnd.setText(TolgasModel.Saturday.secondPairEnd);
                            break;
                        case 3:
                            viewPairStart.setText(TolgasModel.Saturday.thirdPairStart);
                            viewPairEnd.setText(TolgasModel.Saturday.thirdPairEnd);
                            break;
                        case 4:
                            viewPairStart.setText(TolgasModel.Saturday.fourthPairStart);
                            viewPairEnd.setText(TolgasModel.Saturday.fourthPairEnd);
                            break;
                        case 5:
                            viewPairStart.setText(TolgasModel.Saturday.fifthPairStart);
                            viewPairEnd.setText(TolgasModel.Saturday.fifthPairEnd);
                            break;
                    }
                } else {
                    switch (d) {

                        case 1:
                            viewPairStart.setText(TolgasModel.firstPairStart);
                            viewPairEnd.setText(TolgasModel.firstPairEnd);
                            break;
                        case 2:
                            viewPairStart.setText(TolgasModel.secondPairStart);
                            viewPairEnd.setText(TolgasModel.secondPairEnd);
                            break;
                        case 3:
                            viewPairStart.setText(TolgasModel.thirdPairStart);
                            viewPairEnd.setText(TolgasModel.thirdPairEnd);
                            break;
                        case 4:
                            viewPairStart.setText(TolgasModel.fourthPairStart);
                            viewPairEnd.setText(TolgasModel.fourthPairEnd);
                            break;
                        case 5:
                            viewPairStart.setText(TolgasModel.fifthPairStart);
                            viewPairEnd.setText(TolgasModel.fifthPairEnd);
                            break;
                        case 6:
                            viewPairStart.setText(TolgasModel.sixthPairStart);
                            viewPairEnd.setText(TolgasModel.sixthPairEnd);
                            break;
                        case 7:
                            viewPairStart.setText(TolgasModel.seventhPairStart);
                            viewPairEnd.setText(TolgasModel.seventhPairEnd);
                            break;
                    }
                }

                dayLayout.addView(pairLayout);
            }

        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        //вызывается при открытии и закрытии меню
//        //если дровер открыт, то в экшн бар ставится старый заголовок
//        Log.d(TAG, "onCreateOptionsMenu");
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.menu_shedule, menu);
//            restoreActionBar();
//            return true;
//        }
//        // Inflate the menu items for use in the action bar
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    //Вызывается при нажатии кнопки меню
    {
        Log.d(TAG, "onOptionsItemSelected");

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private final static String TAG = "PlaceholderFragment";
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            Log.d(TAG, "newInstance");
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.d(TAG, "onCreateView");
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Context context) {
            Log.d(TAG, "onAttach");
            super.onAttach(context);

            ((SheduleActivity) context).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
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
