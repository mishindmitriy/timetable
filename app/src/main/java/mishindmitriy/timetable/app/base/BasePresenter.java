package mishindmitriy.timetable.app.base;

import android.content.SharedPreferences;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;

import javax.inject.Inject;

import io.realm.Realm;
import mishindmitriy.timetable.model.ScheduleSubject;
import mishindmitriy.timetable.utils.Prefs;

/**
 * Created by mishindmitriy on 06.01.2017.
 */
public abstract class BasePresenter<V extends MvpView> extends MvpPresenter<V>
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Inject
    protected Prefs prefs;
    @Inject
    protected Realm realm;

    protected BasePresenter() {
        super();
        inject();
        prefs.register(this);
    }

    protected abstract void inject();

    protected Prefs getPrefs() {
        return prefs;
    }

    public Realm getRealm() {
        return realm;
    }

    public boolean isSubjectSelected() {
        return prefs.getSelectedThingId() != 0;
    }

    public ScheduleSubject getCurrentSubject() {
        return realm.where(ScheduleSubject.class)
                .equalTo("id", prefs.getSelectedThingId())
                .findFirst();
    }

    @Override
    public void onDestroy() {
        realm.close();
        prefs.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Prefs.KEY_SELECTED_THING_ID)) {
            onSubjectChange();
        }
    }

    protected abstract void onSubjectChange();
}
