package mishindmitriy.timetable.app.base;

import android.content.SharedPreferences;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;

import javax.inject.Inject;

import io.realm.Realm;
import mishindmitriy.timetable.model.ScheduleSubject;
import mishindmitriy.timetable.utils.Prefs;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

/**
 * Created by mishindmitriy on 06.01.2017.
 */
public abstract class BasePresenter<V extends MvpView> extends MvpPresenter<V>
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final BehaviorSubject<ScheduleSubject> currentSubjectObservable = BehaviorSubject.create();
    @Inject
    protected Prefs prefs;
    @Inject
    protected Realm realm;
    private ScheduleSubject currentSubject;
    private Subscription currentSubjectSubscription;

    protected BasePresenter() {
        super();
        inject();
        setCurrentSubject();
        prefs.register(this);
    }

    protected abstract void inject();

    private void setCurrentSubject() {
        if (currentSubjectSubscription != null) currentSubjectSubscription.unsubscribe();
        currentSubject = realm.where(ScheduleSubject.class)
                .equalTo("id", prefs.getSelectedThingId())
                .findFirst();
        Observable<ScheduleSubject> observable = currentSubject.asObservable();
        currentSubjectSubscription = observable.subscribe(new Action1<ScheduleSubject>() {
            @Override
            public void call(ScheduleSubject subject) {
                currentSubjectObservable.onNext(subject);
            }
        });
        currentSubjectObservable.onNext(currentSubject);
    }

    public Observable<ScheduleSubject> getCurrentSubjectObservable() {
        return currentSubjectObservable;
    }

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
        return currentSubject;
    }

    @Override
    public void onDestroy() {
        realm.close();
        prefs.unregister(this);
        super.onDestroy();
    }

    public boolean isSubjectNotNull() {
        return currentSubject != null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Prefs.KEY_SELECTED_THING_ID)) {
            setCurrentSubject();
        }
    }
}
