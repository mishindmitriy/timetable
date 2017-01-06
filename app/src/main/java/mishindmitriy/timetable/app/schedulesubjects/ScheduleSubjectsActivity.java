package mishindmitriy.timetable.app.schedulesubjects;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseActivity;
import mishindmitriy.timetable.app.base.BaseAdapter;
import mishindmitriy.timetable.app.shedule.ScheduleActivity;
import mishindmitriy.timetable.model.ScheduleSubject;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class ScheduleSubjectsActivity extends BaseActivity implements ScheduleSubjectsView {
    protected SearchView searchView;
    protected Toolbar toolbar;
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeRefreshLayout;

    @InjectPresenter
    ScheduleSubjectsPresenter presenter;
    private ScheduleSubjectAdapter scheduleSubjectAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_things);
        initView();
        if (prefs.getSelectedThingId() != 0) {
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadThings();
            }
        });

        scheduleSubjectAdapter = new ScheduleSubjectAdapter(
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
                                subscriber.onNext(newText);
                                return true;
                            }
                        });
                    }
                })
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .onErrorReturn(new Func1<Throwable, String>() {
                            @Override
                            public String call(Throwable throwable) {
                                return "";
                            }
                        })
                        .startWith("")
        );

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
    }

    @Override
    public void showRefreshing() {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void setData(List<ScheduleSubject> data) {
        if (scheduleSubjectAdapter != null) {
            scheduleSubjectAdapter.setData(data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadIfNeed();
    }

    private void onSubjectClicked(ScheduleSubject subject) {
        if (subject == null) return;
        prefs.setSelectedThingId(subject.getId());
        startActivity(new Intent(this, ScheduleActivity.class));
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

    @Override
    protected void onPause() {
        super.onPause();
        hideRefreshing();
    }

    @Override
    public void hideRefreshing() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private void initView() {
        searchView = (SearchView) findViewById(R.id.searchView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    }
}
