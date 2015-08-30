package mishindmitriy.timetable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SheduleActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private String group_id;
    private CharSequence group_name;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shedule);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        //Установим в заголовке 7 дней, тк пока грузим принудительно 7 дней
        mTitle = getString(R.string.SevenDaysShedule);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.sheduleLayout));


        //чтение настроек из файла
        SharedPreferences preferences=getSharedPreferences(String.valueOf(PreferensesConst.APP_PREFERENCES), Context.MODE_PRIVATE);
        group_id=preferences.getString(String.valueOf(PreferensesConst.GROUP_ID), "null");
        group_name=preferences.getString(String.valueOf(PreferensesConst.GROUP_NUMBER),"null");
        //SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.sheduleLayout);
        //progressBar=new ProgressBar(SheduleActivity.this);





        //если в настройках нет записи, то запускаем активность со списком групп
        if (group_id != null && group_id.contains("null"))
        {
            Intent intent = new Intent(this,CaseGroupActivity.class);
            finish();
            startActivity(intent);
        }
        else {
            setTitle(group_name);
            //swipeRefreshLayout.setRefreshing(true);
            new ParseShedule().execute(group_id);
        }


//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new ParseShedule().execute(group_id);
//            }
//        });

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                //mTitle = getString(R.string.TodayShedule);
                break;
            case 2:
                //mTitle = getString(R.string.TomorowShedule);
                break;
            case 3:
                //mTitle = getString(R.string.SevenDaysShedule);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(group_name+" , "+mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class ParseShedule extends AsyncTask<String, Void, List<DayPairs>> {

        private ProgressBar progressBar;
        @Override
        protected void onPreExecute() {
           // SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.sheduleLayout);
           // swipeRefreshLayout.setRefreshing(true);
            progressBar//=(ProgressBar) findViewById(R.id.progressBar);
            =new ProgressBar(SheduleActivity.this);


            LinearLayout sheduleLayout=(LinearLayout) findViewById(R.id.dayPairsLayoutOnActivity);
            sheduleLayout.addView(progressBar,0);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setEnabled(true);
            super.onPreExecute();
        }

        protected List<DayPairs> doInBackground(String... arg) {
            List<DayPairs> list=new ArrayList<>();

            try
            {
                list=TolgasModel.getTodaySheduleByIdGroup(arg[0]);//передаем id группы
                if (list==null) {
                    return new ArrayList<>();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
            return list;
        }

        protected void onPostExecute(List<DayPairs> output) {

           // SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.sheduleLayout);
            if (output==null)
            {
                //swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(SheduleActivity.this,"Загрузить данные не удалось. Проверьте интернет-соединение",Toast.LENGTH_LONG).show();
                return;
            }

            LinearLayout dayslayout=(LinearLayout) findViewById(R.id.dayPairsLayoutOnActivity);
            dayslayout.removeAllViews();
            LayoutInflater layoutInflater=getLayoutInflater();

            for(int i=0; i<output.size();i++)
            {
                LinearLayout dayLayout= (LinearLayout) layoutInflater.inflate(R.layout.day_pairs_layout, null, false);
                TextView viewDate= (TextView) dayLayout.findViewById(R.id.textViewDate);
                Date date=new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    date=sdf.parse(output.get(i).getDate().toString());


                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal=Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);
                cal.setTime(date);
                int d=cal.get(Calendar.DAY_OF_WEEK);
                String dayOfWeek="Понедельник";
                switch (d)
                {
                    case Calendar.TUESDAY:
                        dayOfWeek="Вторник";
                        break;
                    case Calendar.WEDNESDAY:
                        dayOfWeek="Среда";
                        break;
                    case Calendar.THURSDAY:
                        dayOfWeek="Четверг";
                        break;
                    case Calendar.FRIDAY:
                        dayOfWeek="Пятница";
                        break;
                    case Calendar.SATURDAY:
                        dayOfWeek="Суббота";
                        break;
                    case Calendar.SUNDAY:
                        dayOfWeek="Воскресенье";
                        break;
                }
                viewDate.setText(sdf.format(date)+", "+dayOfWeek);
                dayslayout.addView(dayLayout);

                for (int n=0; n<output.get(i).getPairsArray().size(); n++)
                {
                    RelativeLayout pairLayout=(RelativeLayout) layoutInflater.inflate(R.layout.pair, null, false);
                    TextView viewClassroom= (TextView) pairLayout.findViewById(R.id.textViewClassroom);
                    viewClassroom.setText(output.get(i).getPair(n).getClassroom());
                    TextView viewSubject= (TextView) pairLayout.findViewById(R.id.textViewSubject);
                    viewSubject.setText(output.get(i).getPair(n).getSubject());
                    TextView viewPairNumber= (TextView) pairLayout.findViewById(R.id.textViewPairNumber);
                    viewPairNumber.setText(output.get(i).getPair(n).getPairNumber());
                    TextView viewPrepod= (TextView) pairLayout.findViewById(R.id.textViewPrepod);
                    viewPrepod.setText(output.get(i).getPair(n).getPrepod());
                    TextView viewTypePair= (TextView) pairLayout.findViewById(R.id.textViewTypePair);
                    viewTypePair.setText(output.get(i).getPair(n).getTypePair());

                    dayLayout.addView(pairLayout);
                }

            }
            //swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            progressBar.setEnabled(false);
            LinearLayout sheduleLayout=(LinearLayout) findViewById(R.id.dayPairsLayoutOnActivity);
            //sheduleLayout.removeViewAt(0);
            if (output.size()==0) Toast.makeText(SheduleActivity.this,"На 7 дней вперед занятий нет",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_shedule, menu);
            restoreActionBar();
            return true;
        }
        // Inflate the menu items for use in the action bar
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_caseGroup:
                Intent intent = new Intent(this,CaseGroupActivity.class);
                finish();
                startActivity(intent);
                return true;
            case R.id.action_example:
                new ParseShedule().execute(group_id);
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
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((SheduleActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
