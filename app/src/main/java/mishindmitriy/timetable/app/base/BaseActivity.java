package mishindmitriy.timetable.app.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.arellomobile.mvp.MvpAppCompatActivity;

import javax.inject.Inject;

import io.realm.Realm;
import mishindmitriy.timetable.app.TimeTableApp;
import mishindmitriy.timetable.utils.Prefs;

/**
 * Created by mishindmitriy on 02.07.2016.
 */
public abstract class BaseActivity extends MvpAppCompatActivity {
    @Inject
    @Deprecated
    protected Prefs prefs;
    @Inject
    @Deprecated
    protected Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TimeTableApp.component().inject(this);
    }

    @Override
    protected void onDestroy() {
        if (realm != null) {
            realm.close();
            realm = null;
        }
        super.onDestroy();
    }

    @Deprecated
    public Realm getRealm() {
        return realm;
    }
}
