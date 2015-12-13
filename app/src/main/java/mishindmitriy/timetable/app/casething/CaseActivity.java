package mishindmitriy.timetable.app.casething;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.List;

import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.R;
import mishindmitriy.timetable.model.data.entity.Thing;
import mishindmitriy.timetable.model.db.HelperFactory;

@EActivity(R.layout.activity_case)
public class CaseActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    private static final String TAG = "CaseActivity";

    @ViewById(R.id.pager)
    ViewPager mViewPager;
    @ViewById(R.id.tabs)
    TabLayout tabLayout;
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @Extra
    boolean firstTime;
    List<Thing> favorites;

    @AfterViews
    protected void init() {
        if (!firstTime) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            toolbar.inflateMenu(R.menu.menu_case_thing_forward);
            toolbar.setOnMenuItemClickListener(this);
        }

        ThingsPagerAdapter pagerAdapter = new ThingsPagerAdapter(this.getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);

        if (!BuildConfig.DEBUG){
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Экран выбора групп"));
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Просмотр списка групп"));
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position)
                {
                    case 0:
                        if (!BuildConfig.DEBUG){
                            Answers.getInstance().logContentView(new ContentViewEvent()
                                    .putContentName("Просмотр списка групп"));
                        }
                        break;
                    case 1:
                        if (!BuildConfig.DEBUG){
                            Answers.getInstance().logContentView(new ContentViewEvent()
                                    .putContentName("Просмотр списка преподавателей"));
                        }
                        break;
                    case 2:
                        if (!BuildConfig.DEBUG){
                            Answers.getInstance().logContentView(new ContentViewEvent()
                                    .putContentName("Просмотр списка аудиторий"));
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        forwardUpdate();
    }

    @Override
    public void onBackPressed() {
        if (favoritesAvailable()) {
            setResult(0);
            finish();
        } else {
            Toast.makeText(this, R.string.case_least_one_group, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean favoritesAvailable() {
        return favorites!=null && favorites.size()>0;
    }

    @Background
    public void forwardUpdate() {
        try {
            favorites = HelperFactory.getInstance().getThingGAO().loadFavorites();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        toolbarUpdate();
    }

    @UiThread
    void toolbarUpdate()
    {
        if (toolbar.getMenu().size() > 0) {
            if (favoritesAvailable()) {
                toolbar.getMenu().getItem(0).setVisible(true);
            } else {
                toolbar.getMenu().getItem(0).setVisible(false);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        onBackPressed();
        return true;
    }
}
