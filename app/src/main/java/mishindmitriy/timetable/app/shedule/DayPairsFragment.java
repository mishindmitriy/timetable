package mishindmitriy.timetable.app.shedule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.joda.time.LocalDate;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseFragment;
import mishindmitriy.timetable.model.Pair;
import mishindmitriy.timetable.utils.DataHelper;
import mishindmitriy.timetable.utils.Prefs;

/**
 * Created by mishindmitriy on 20.09.2016.
 */
@EFragment(R.layout.recycler_view)
public class DayPairsFragment extends BaseFragment {
    @ViewById(R.id.recyclerView)
    protected RecyclerView recyclerView;
    @ViewById(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @FragmentArg
    protected LocalDate localDate = null;
    private PairAdapter adapter = new PairAdapter();
    private SharedPreferences.OnSharedPreferenceChangeListener listener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Prefs.KEY_SELECTED_THING_SERVER_ID)) {
                loadData();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Prefs.get().register(listener);
    }

    @Override
    public void onDestroy() {
        Prefs.get().unregister(listener);
        super.onDestroy();
    }

    @AfterViews
    protected void init() {
        if (localDate == null) {
            throw new IllegalArgumentException("localdate must not be null");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                DataHelper.loadSchedule(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void loadData() {
        adapter.setData(realm.where(Pair.class)
                .equalTo("date", localDate.toString())
                .equalTo("thing.serverId", Prefs.get().getSelectedThingServerId())
                .findAllSortedAsync("number"));
    }
}
