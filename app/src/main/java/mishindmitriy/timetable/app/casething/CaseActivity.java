package mishindmitriy.timetable.app.casething;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.List;

import mishindmitriy.timetable.R;
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

        forwardUpdate();
    }

    @Override
    public void onBackPressed() {
        if (forwardUpdate()) {
            setResult(0);
            finish();
        } else {
            //Snackbar.make(mViewPager, "Выберите хотя-бы одну группу", Snackbar.LENGTH_LONG).show();
            Toast.makeText(this, "Выберите хотя-бы одну группу", Toast.LENGTH_SHORT).show();
        }
        //super.onBackPressed();
    }

    public boolean forwardUpdate() {
        List favorites = null;
        try {
            favorites = HelperFactory.getInstance().getThingGAO().loadFavorites();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (favorites != null && favorites.size() > 0) {
            if (toolbar.getMenu().size() > 0) toolbar.getMenu().getItem(0).setVisible(true);
            return true;
        } else {
            if (toolbar.getMenu().size() > 0) toolbar.getMenu().getItem(0).setVisible(false);
            return false;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        onBackPressed();
        return true;
    }
}
