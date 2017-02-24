package mishindmitriy.timetable.app.schedulesubjects;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.View;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.List;
import java.util.concurrent.TimeUnit;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseAdapter;
import mishindmitriy.timetable.app.shedule.ScheduleActivity;
import mishindmitriy.timetable.databinding.ActivityScheduleSubjectsBinding;
import mishindmitriy.timetable.model.ScheduleSubject;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class ScheduleSubjectsActivity extends MvpAppCompatActivity implements ScheduleSubjectsView {
    private static final String KEY_QUERY = "search_query";
    @InjectPresenter
    ScheduleSubjectsPresenter presenter;
    private ScheduleSubjectAdapter scheduleSubjectAdapter;
    private ActivityScheduleSubjectsBinding binding;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_QUERY, binding.searchView.getQuery().toString());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_subjects);

        if (presenter.isSubjectSelected()) {
            binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        binding.searchView.onActionViewExpanded();
        binding.searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadThings();
            }
        });

        if (savedInstanceState != null) {
            binding.searchView.setQuery(savedInstanceState.getString(KEY_QUERY), false);
        }
        scheduleSubjectAdapter = new ScheduleSubjectAdapter();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(scheduleSubjectAdapter);
        binding.recyclerView.addItemDecoration(decoration);
        scheduleSubjectAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });
        binding.recyclerView.setAdapter(scheduleSubjectAdapter);

        scheduleSubjectAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<ScheduleSubject>() {
            @Override
            public void onItemClick(final ScheduleSubject subject) {
                presenter.onSubjectClicked(subject);
            }
        });

        presenter.setSearchObservable(createSearchObservable());
    }

    private Observable<String> createSearchObservable() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        subscriber.onNext(query);
                        return true;
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
                .startWith(binding.searchView.getQuery().toString());
    }

    @Override
    public void startScheduleActivity() {
        startActivity(new Intent(this, ScheduleActivity.class));
        finish();
    }

    @Override
    public void setRefreshing(final boolean enable) {
        if (binding.swipeRefreshLayout != null) {
            binding.swipeRefreshLayout.setRefreshing(enable);
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        setRefreshing(false);
    }
}
