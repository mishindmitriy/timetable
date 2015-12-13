package mishindmitriy.timetable.app.shedule;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.ObjectAdapter;
import mishindmitriy.timetable.app.casething.CaseActivity_;
import mishindmitriy.timetable.app.shedule.widgets.ItemFavoriteThingView;
import mishindmitriy.timetable.app.shedule.widgets.ItemFavoriteThingView_;
import mishindmitriy.timetable.model.SheduleModel;
import mishindmitriy.timetable.model.SheduleWorkerFragment;
import mishindmitriy.timetable.model.data.entity.Pair;
import mishindmitriy.timetable.model.data.entity.Thing;
import mishindmitriy.timetable.utils.FrendlyDeviceNameUtil;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

@EActivity(R.layout.activity_shedule)
@OptionsMenu(R.menu.menu_shedule)
public class SheduleActivity extends AppCompatActivity
        implements SheduleModel.Observer, AdapterView.OnItemSelectedListener {

    final static int CODE = 1;
    private static final String TAG = "SheduleActivity";
    private static final String TAG_WORKER = "SheduleModelWorkerTAG";
    private static final long HOUR = 3600000;
    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;
    @ViewById(R.id.sheduleSwipeRefresh)
    protected SwipeRefreshLayout mSwipeLayout;
    @ViewById(R.id.sheduleLayout)
    protected DrawerLayout mDrawerLayout;
    @ViewById(R.id.list_favorites_thing)
    protected ListView favoritesListView;
    @ViewById(R.id.dayPairsLayoutInActivity)
    protected StickyListHeadersListView mListShedule;
    @ViewById(R.id.current_thing_title)
    protected TextView currentThingTextView;
    @ViewById(R.id.spinner)
    protected Spinner spinner;
    @ViewById(R.id.scrollView)
    protected ScrollView scrollView;
    @ViewById(R.id.nvView)
    protected NavigationView navigationView;
    @Extra
    protected Thing thing;
    @FragmentByTag(TAG_WORKER)
    protected SheduleWorkerFragment sheduleWorkerFragment;
    @InstanceState
    protected Date lastUpdate;
    @ViewById(R.id.empty)
    TextView emptyText;
    @ViewById(R.id.add_favorites)
    TextView addFavorites;
    private SheduleModel mSheduleModel;
    private ActionBarDrawerToggle mDrawerToggle;
    private SheduleListAdapter mSheduleAdapter;

    @OnActivityResult(CODE)
    void onResult() {
        mSheduleModel.checkCurrentThing();
        refreshFavorites();
        loadDataWithChecks();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSheduleModel.stopLoad();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataWithChecks();
    }

    private boolean canUpdate() {
        return lastUpdate == null || (new Date().getTime() - lastUpdate.getTime()) > HOUR;
    }

    @AfterViews
    protected void init() {
        emptyText.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        getWindow().setBackgroundDrawable(null);

        Drawable plus = addFavorites.getCompoundDrawables()[0];
        if (plus != null)
            plus.setColorFilter(getResources().getColor(R.color.select), PorterDuff.Mode.MULTIPLY);

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
                mSheduleModel = workerFragment.getSheduleModel();
                if (thing != null) mSheduleModel.setThing(thing);
            }
            this.mSheduleModel.registerObserver(this);

            if (!mSheduleModel.isThingAvailable()) {
                CaseActivity_.intent(this).firstTime(true).startForResult(CODE);
            }
        }

        if (!BuildConfig.DEBUG) {
            String[] periods = getResources().getStringArray(R.array.period_array);
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Экран просмотра раписания")
                    .putCustomAttribute("Период", periods[mSheduleModel.getPeriodPosition()]));
        }

        {
            this.mSwipeLayout.setSoundEffectsEnabled(true);
            this.mSwipeLayout.setColorSchemeResources(R.color.teal500);
            this.mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!BuildConfig.DEBUG) {
                        Answers.getInstance().logCustom(new CustomEvent("Обновление")
                                .putCustomAttribute("Тип", "pull to refresh"));
                    }
                    SheduleActivity.this.mSheduleModel.loadData();
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
            ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.period_array, R.layout.spinner);
            arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinner.setAdapter(arrayAdapter);
            spinner.setSelection(mSheduleModel.getPeriodPosition());
            spinner.setOnItemSelectedListener(this);
        }

        mSheduleAdapter = new SheduleListAdapter(this, mSheduleModel.getShedule());
        mListShedule.setAdapter(mSheduleAdapter);


        {   //init favorites
            currentThingTextView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            refreshFavorites();
        }

        onCurrentThingChange(mSheduleModel.getCurrentThing());

        if (mSheduleModel.isWorking()) onLoadStarted();
        loadDataWithChecks();
    }

    @Click(R.id.add_favorites)
    void addFavoritesClick() {
        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logCustom(new CustomEvent("Нажали +добавить в боковом меню"));
        }
        addToFavorites();
    }

    @OptionsItem(R.id.case_button)
    void caseClick() {
        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logCustom(new CustomEvent("Нажали выбрать группу"));
        }
        addToFavorites();
    }

    private void addToFavorites() {
        CaseActivity_.intent(this).startForResult(CODE);
        mDrawerLayout.closeDrawers();
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
    public final void onLoadStarted() {
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);
            }
        });
        //setFavoritesIcon();
    }

    @Override
    public void onCacheLoad(List<Pair> shedule) {
        if (shedule != null || shedule.size() != 0)
            emptyText.setVisibility(View.GONE);
        mSheduleAdapter.setData(shedule);
    }

    @Override
    public void onLoadFail() {
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
            }
        });
        Snackbar.make(mSwipeLayout, getString(R.string.load_error), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCurrentThingChange(Thing thing) {
        if (thing != null) currentThingTextView.setText(thing.getName());
    }

    @Override
    public void onLoadFinished(List<Pair> shedule) {
        if (shedule == null || shedule.size() == 0) {
            emptyText.setVisibility(View.VISIBLE);
        } else emptyText.setVisibility(View.GONE);
        if (shedule != null && mSheduleAdapter.getCount() != shedule.size()) {
            mListShedule.setSelection(0);
        }
        mSheduleAdapter.setData(shedule);
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
            }
        });
        lastUpdate = new Date();
    }

    @OptionsItem(R.id.refresh_icon)
    void refreshClick() {
        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logCustom(new CustomEvent("Обновление")
                    .putCustomAttribute("Тип", "кнопка в боковом меню"));
        }
        mSheduleModel.loadData();
    }

    @Override
    protected void onDestroy() {
        if (this.mSheduleModel == null) return;
        if (this.isFinishing()) {
            this.mSheduleModel.stopLoad();
        }
        this.mSheduleModel.unregisterObserver(this);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!BuildConfig.DEBUG) {
            String[] periods = getResources().getStringArray(R.array.period_array);
            Answers.getInstance().logCustom(new CustomEvent("Смена периода")
                    .putCustomAttribute("Период", periods[position]));
        }
        if (position != mSheduleModel.getPeriodPosition()) {
            mSheduleModel.setPeriod(position);
            mSheduleModel.loadData();
        }
    }

    private void loadDataWithChecks() {
        if (!mSheduleModel.isWorking() && canUpdate()) {
            mSheduleModel.loadData();
        }
    }

    private void refreshFavorites() {
        List<Thing> favorites = mSheduleModel.getFavoritesThings();
        favoritesListView.setAdapter(new FavoritesAdapter(favorites));
    }

    @ItemClick(R.id.list_favorites_thing)
    protected void favoritesClick(Thing thing) {
        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logCustom(new CustomEvent("Выбор группы из закладок")
                    .putCustomAttribute("Группа", thing.toString()));
        }
        if (thing.getId() != mSheduleModel.getCurrentThing().getId()) {
            mSheduleModel.setThing(thing);
            mSheduleAdapter.setData(new ArrayList<Pair>());
            mSheduleModel.loadData();
        }
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @OptionsItem(R.id.feedback)
    void feedbackClick() {
        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logCustom(new CustomEvent("Нажали отправить отзыв"));
        }
        try {
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"mishin.dmitriy@gmail.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_title));
            emailIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    String.format("\n\n\n %s \n Android %s \n Расписание ПВГУС %d",
                            FrendlyDeviceNameUtil.getDeviceName(),
                            Build.VERSION.RELEASE,
                            BuildConfig.VERSION_CODE));
            startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.title_intent_send_mail)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, String.valueOf(R.string.action_not_possible), Toast.LENGTH_LONG).show();
        }
    }

    @OptionsItem(R.id.rate)
    void rateClick() {
        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logCustom(new CustomEvent("Нажали оценить"));
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            try {
                String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, String.valueOf(R.string.action_not_possible), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class FavoritesAdapter extends ObjectAdapter<Thing> {

        public FavoritesAdapter(List<Thing> list) {
            super(list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ItemFavoriteThingView_.build(parent.getContext());
            }
            ItemFavoriteThingView view = (ItemFavoriteThingView) convertView;
            view.setThing(getItem(position));
            return view;
        }
    }
}
