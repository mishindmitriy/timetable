package mishindmitriy.timetable.app.casething;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import mishindmitriy.timetable.R;

@EActivity(R.layout.activity_case)
public class CaseActivity extends FragmentActivity {
    private static final String TAG = "CaseActivity";

    @ViewById(R.id.pager)
    protected ViewPager mViewPager;
    @ViewById(R.id.tabs)
    protected TabLayout tabLayout;

    @AfterViews
    protected void init() {
        ThingsPagerAdapter pagerAdapter = new ThingsPagerAdapter(this.getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
