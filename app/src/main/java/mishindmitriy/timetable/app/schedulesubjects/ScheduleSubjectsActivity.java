package mishindmitriy.timetable.app.schedulesubjects;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.Sort;
import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseActivity;
import mishindmitriy.timetable.app.base.BaseAdapter;
import mishindmitriy.timetable.app.shedule.ScheduleActivity_;
import mishindmitriy.timetable.model.ScheduleSubject;
import mishindmitriy.timetable.model.ScheduleSubjectType;
import mishindmitriy.timetable.utils.DataHelper;
import mishindmitriy.timetable.utils.Prefs;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@EActivity(R.layout.activity_things)
public class ScheduleSubjectsActivity extends BaseActivity {
    private static final long UPDATE_INTERVAL = 1000 * 60 * 60; //one hour
    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;
    @ViewById(R.id.searchView)
    protected SearchView searchView;
    @ViewById(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @ViewById(R.id.recyclerView)
    protected RecyclerView recyclerView;
    private ScheduleSubjectAdapter scheduleSubjectAdapter = new ScheduleSubjectAdapter();
    private Observable<List<ScheduleSubject>> loadSubjectsObservable;
    private CompositeSubscription subscription = new CompositeSubscription();

    @AfterViews
    protected void init() {
        if (Prefs.get().getSelectedThingId() != 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        searchView.onActionViewExpanded();
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        initSearchObservable();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadThings();
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(scheduleSubjectAdapter);
        recyclerView.addItemDecoration(decoration);
        scheduleSubjectAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });
        recyclerView.setAdapter(scheduleSubjectAdapter);

        scheduleSubjectAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<ScheduleSubject>() {
            @Override
            public void onItemClick(final ScheduleSubject subject) {
                onSubjectClicked(subject);
            }
        });
        scheduleSubjectAdapter.setData(realm.where(ScheduleSubject.class)
                .findAllSortedAsync("sortRating", Sort.ASCENDING, "name", Sort.ASCENDING));

        initLoadObservable();
    }

    private void initLoadObservable() {
        loadSubjectsObservable = Observable.zip(
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canUpdate()) {
            swipeRefreshLayout.setRefreshing(true);
            loadThings();
        }
    }

    private void initSearchObservable() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(newText);
                        }
                        return false;
                    }
                });
            }
        })
                .debounce(100, TimeUnit.MILLISECONDS)
                .onErrorReturn(new Func1<Throwable, String>() {
                    @Override
                    public String call(Throwable throwable) {
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        scheduleSubjectAdapter.filter(s);
                    }
                });
    }

    private void onSubjectClicked(ScheduleSubject subject) {
        if (subject == null) return;
        Prefs.get().setSelectedThingId(subject.getId());
        ScheduleActivity_.intent(ScheduleSubjectsActivity.this).start();
        finish();
        final Long id = subject.getId();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ScheduleSubject currentScheduleSubject = realm.where(ScheduleSubject.class)
                        .equalTo("id", id)
                        .findFirst();
                currentScheduleSubject.incrementOpenTimes();
            }
        });
    }

    private void loadThings() {
        subscription.clear();
        subscription.add(loadSubjectsObservable
                .subscribe(new Subscriber<List<ScheduleSubject>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideRefreshing();
                    }

                    @Override
                    public void onNext(List<ScheduleSubject> scheduleSubjects) {
                        if (scheduleSubjects.size() > 0) {
                            Prefs.get().setSubjectsLastUpdate(DateTime.now().getMillis());
                        }
                        hideRefreshing();
                    }
                })
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        subscription.clear();
        hideRefreshing();
    }


    private boolean canUpdate() {
        return DateTime.now().getMillis() - Prefs.get().getSubjectsLastUpdate() > UPDATE_INTERVAL;
    }

    private void hideRefreshing() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
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
}
