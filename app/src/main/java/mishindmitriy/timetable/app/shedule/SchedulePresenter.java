package mishindmitriy.timetable.app.shedule;

import android.content.SharedPreferences;

import com.arellomobile.mvp.InjectViewState;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import mishindmitriy.timetable.app.TimeTableApp;
import mishindmitriy.timetable.app.base.BasePresenter;
import mishindmitriy.timetable.model.Pair;
import mishindmitriy.timetable.model.ScheduleSubject;
import mishindmitriy.timetable.utils.DataHelper;
import mishindmitriy.timetable.utils.Prefs;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mishindmitriy on 06.01.2017.
 */
@InjectViewState
public class SchedulePresenter extends BasePresenter<ScheduleView> {
    private final Observable<List<Pair>> dataUpdateObservable;
    protected LocalDate startDate = LocalDate.now();
    private Subscription dataUpdateSubscription;

    public SchedulePresenter() {
        super();
        getCurrentSubjectObservable()
                .subscribe(new Action1<ScheduleSubject>() {
                    @Override
                    public void call(ScheduleSubject scheduleSubject) {
                        getViewState().showCurrentSubjectTitle(getCurrentSubject().getName());
                    }
                });
        dataUpdateObservable = createLoadPairsObservable(startDate);
    }

    @Override
    protected void inject() {
        TimeTableApp.component().inject(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        super.onSharedPreferenceChanged(sharedPreferences, key);
        if (key.equals(Prefs.KEY_SELECTED_THING_ID)) {
            onDateSelected(LocalDate.now());
        }
    }

    public void onDateSelected(LocalDate newDate) {
        if (!startDate.isEqual(newDate)) {
            startDate = newDate;
            if (canUpdate() || !isPairsContainsForDate(newDate)) refreshData();
            getViewState().setStartDate(newDate);
        }
        getViewState().notifyPagerDateChanged();
       /* Log.d("testtt", "date selected service");
        startService(new Intent(this, NotificationService.class));*/
    }

    private boolean isPairsContainsForDate(LocalDate date) {
        return realm.where(Pair.class)
                .equalTo("date", date.toString())
                .equalTo("scheduleSubject.id", prefs.getSelectedThingId())
                .count() > 0;
    }

    private boolean canUpdate() {
        final long DAY = 3600000 * 24;
        return DateTime.now().getMillis() - prefs.getScheduleLastUpdate() > DAY;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().showCurrentSubjectTitle(getCurrentSubject().getName());
        realm.where(ScheduleSubject.class)
                .greaterThan("timesOpen", 0)
                .findAllSortedAsync("timesOpen", Sort.DESCENDING, "name", Sort.ASCENDING)
                .asObservable()
                .subscribe(new Action1<RealmResults<ScheduleSubject>>() {
                    @Override
                    public void call(RealmResults<ScheduleSubject> scheduleSubjects) {
                        getViewState().setSubjectsData(scheduleSubjects);
                    }
                });
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Observable<List<Pair>> createLoadPairsObservable(LocalDate startDate) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        final LocalDate from = startDate.minusDays(50);
        final LocalDate to = startDate.plusDays(50);
        return Observable.create(new Observable.OnSubscribe<List<Pair>>() {
            @Override
            public void call(Subscriber<? super List<Pair>> subscriber) {
                long id = prefs.getSelectedThingId();
                if (id == 0) throw new IllegalStateException();

                Realm realm = Realm.getDefaultInstance();
                ScheduleSubject scheduleSubject = null;
                try {
                    scheduleSubject = realm.where(ScheduleSubject.class)
                            .equalTo("id", id)
                            .findFirst();
                    if (scheduleSubject == null) throw new IllegalStateException();

                    List<Pair> pairs = DataHelper.getShedule(scheduleSubject, from, to);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(pairs);
                    }
                } catch (IOException e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                } finally {
                    realm.close();
                }
            }
        })
                .timeout(10, TimeUnit.SECONDS)
                .onErrorReturn(new Func1<Throwable, List<Pair>>() {
                    @Override
                    public List<Pair> call(Throwable throwable) {
                        return new ArrayList<Pair>();
                    }
                })
                .doOnNext(new Action1<List<Pair>>() {
                    @Override
                    public void call(final List<Pair> pairs) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                for (Pair p : pairs) {
                                    Pair existPair = realm.where(Pair.class)
                                            .equalTo("id", p.getId())
                                            .findFirst();
                                    if (existPair != null && existPair.isNotified()) {
                                        p.setNotified();
                                    }
                                }
                                realm.copyToRealmOrUpdate(pairs);
                                // TODO: 19.09.16 add remove old pairs for this period (change date type to millis?)
                            }
                        });
                        realm.close();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void loadIfNeed() {
        if (canUpdate()) {
            refreshData();
        }
    }

    public void refreshData() {
        getViewState().setRefreshing(true);
        if (dataUpdateSubscription != null && !dataUpdateSubscription.isUnsubscribed()) {
            dataUpdateSubscription.unsubscribe();
        }
        dataUpdateSubscription = dataUpdateObservable.subscribe(new Subscriber<List<Pair>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getViewState().setRefreshing(false);
            }

            @Override
            public void onNext(List<Pair> pairs) {
                prefs.setScheduleLastUpdate();
                getViewState().setRefreshing(false);
               /* if (pairs != null && pairs.size() > 0) {
                    Log.d("testtt", "onnext service");
                    startService(new Intent(ScheduleActivity.this, NotificationService.class));
                }*/
            }
        });
    }

    public void scheduleSubjectClicked(ScheduleSubject scheduleSubject) {
        if (scheduleSubject.getId() != prefs.getSelectedThingId()) {
            setNewThing(scheduleSubject);
        }
    }

    private void setNewThing(final ScheduleSubject scheduleSubject) {
        if (scheduleSubject == null) return;
        prefs.setSelectedThingId(scheduleSubject.getId());
        final Long id = scheduleSubject.getId();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(ScheduleSubject.class)
                        .equalTo("id", id)
                        .findFirst()
                        .incrementOpenTimes();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                /*Log.d("testtt", "change subject service");
                startService(new Intent(ScheduleActivity.this, NotificationService.class));*/
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });
    }
}
