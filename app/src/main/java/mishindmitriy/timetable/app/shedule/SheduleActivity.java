package mishindmitriy.timetable.app.shedule;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseActivity;
import mishindmitriy.timetable.app.things.ThingsActivity_;
import mishindmitriy.timetable.model.Pair;
import mishindmitriy.timetable.model.Thing;
import mishindmitriy.timetable.utils.DataHelper;
import mishindmitriy.timetable.utils.Prefs;

@EActivity(R.layout.activity_shedule)
@OptionsMenu(R.menu.menu_shedule)
public class SheduleActivity extends BaseActivity {
    private static final long HOUR = 3600000;
    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;
    @ViewById(R.id.sheduleLayout)
    protected DrawerLayout mDrawerLayout;
    @ViewById(R.id.current_thing_title)
    protected TextView currentThingTextView;
    @ViewById(R.id.nvView)
    protected NavigationView navigationView;
    @InstanceState
    protected Date lastUpdate;
    private ActionBarDrawerToggle mDrawerToggle;

    private boolean canUpdate() {
        return lastUpdate == null || (new Date().getTime() - lastUpdate.getTime()) > HOUR;
    }

    @AfterViews
    protected void init() {
        if (Prefs.get().getSelectedThingServerId() == null) {
            ThingsActivity_.intent(this).start();
            finish();
            return;
        }

        List<Pair> pairs = realm.where(Pair.class).findAll();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                String serverId = Prefs.get().getSelectedThingServerId();
                Thing thing = realm.where(Thing.class)
                        .equalTo("serverId", serverId)
                        .findFirst();
                try {
                    List<Pair> pairs = DataHelper.getShedule(thing,
                            LocalDate.now().minusDays(100),
                            LocalDate.now().plusDays(100));
                    realm.copyToRealmOrUpdate(pairs);
                    // TODO: 19.09.16 add remove old pairs
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
