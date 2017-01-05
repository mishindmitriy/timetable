package mishindmitriy.timetable.app.schedulesubjects;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.realm.Realm;
import mishindmitriy.timetable.app.TimeTableApp;
import mishindmitriy.timetable.model.ScheduleSubject;
import mishindmitriy.timetable.model.ScheduleSubjectType;
import mishindmitriy.timetable.utils.DataHelper;
import mishindmitriy.timetable.utils.Prefs;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by mishindmitriy on 03.01.2017.
 */
@InjectViewState
public class ScheduleSubjectsPresenter extends MvpPresenter<ScheduleSubjectsView> {
    private static final long UPDATE_INTERVAL = 1000 * 60 * 60; //one hour
    @Inject
    protected Prefs prefs;
    private Observable<List<ScheduleSubject>> loadSubjectsObservable = Observable.zip(
            createLoadThingObservable(ScheduleSubjectType.GROUP),
            createLoadThingObservable(ScheduleSubjectType.TEACHER),
            createLoadThingObservable(ScheduleSubjectType.CLASSROOM),
            new Func3<List<ScheduleSubject>, List<ScheduleSubject>,
                    List<ScheduleSubject>, List<ScheduleSubject>>() {
                @Override
                public List<ScheduleSubject> call(List<ScheduleSubject> subjects1,
                                                  List<ScheduleSubject> subjects2,
                                                  List<ScheduleSubject> subjects3) {
                    List<ScheduleSubject> allScheduleSubjects = new ArrayList<>();
                    allScheduleSubjects.addAll(subjects1);
                    allScheduleSubjects.addAll(subjects2);
                    allScheduleSubjects.addAll(subjects3);
                    return allScheduleSubjects;
                }
            }
    )
            .doOnNext(new Action1<List<ScheduleSubject>>() {
                @Override
                public void call(List<ScheduleSubject> scheduleSubjects) {
                    cacheSubjects(scheduleSubjects);
                }
            })
            .timeout(10, TimeUnit.SECONDS)
            .onErrorReturn(new Func1<Throwable, List<ScheduleSubject>>() {
                @Override
                public List<ScheduleSubject> call(Throwable throwable) {
                    return null;
                }
            })
            .subscribeOn(Schedulers.io());
    private CompositeSubscription subscriptions = new CompositeSubscription();

    public ScheduleSubjectsPresenter() {
        super();
        TimeTableApp.component().inject(this);
    }

    @Override
    public void onDestroy() {
        subscriptions.clear();
        super.onDestroy();
    }

    public void loadThings() {
        subscriptions.clear();
        subscriptions.add(loadSubjectsObservable
                .subscribe(new Subscriber<List<ScheduleSubject>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getViewState().hideRefreshing();
                    }

                    @Override
                    public void onNext(List<ScheduleSubject> scheduleSubjects) {
                        if (scheduleSubjects.size() > 0) {
                            prefs.setSubjectsLastUpdate(DateTime.now().getMillis());
                        }
                        getViewState().hideRefreshing();
                    }
                })
        );
    }

    private boolean canUpdate() {
        return DateTime.now().getMillis() - prefs.getSubjectsLastUpdate() > UPDATE_INTERVAL
                && !subscriptions.hasSubscriptions();
    }

    private void cacheSubjects(final List<ScheduleSubject> scheduleSubjects) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (ScheduleSubject s : scheduleSubjects) {
                    s.mergeWithExistObject(realm);
                }

                realm.copyToRealmOrUpdate(scheduleSubjects);
            }
        });
        realm.close();
    }

    private Observable<List<ScheduleSubject>> createLoadThingObservable(@NonNull final ScheduleSubjectType scheduleSubjectType) {
        return Observable.create(new Observable.OnSubscribe<List<ScheduleSubject>>() {
            @Override
            public void call(Subscriber<? super List<ScheduleSubject>> subscriber) {
                try {
                    List<ScheduleSubject> scheduleSubjects = DataHelper.loadThing(scheduleSubjectType);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(scheduleSubjects);
                    }
                } catch (IOException e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        })
                .timeout(5, TimeUnit.SECONDS)
                .onErrorReturn(new Func1<Throwable, List<ScheduleSubject>>() {
                    @Override
                    public List<ScheduleSubject> call(Throwable throwable) {
                        return new ArrayList<ScheduleSubject>();
                    }
                });
    }


    public void loadIfNeed() {
        if (canUpdate()) {
            getViewState().showRefreshing();
            loadThings();
        }
    }
}
