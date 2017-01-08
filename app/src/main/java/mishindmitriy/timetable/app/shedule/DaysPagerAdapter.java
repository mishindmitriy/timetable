package mishindmitriy.timetable.app.shedule;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import mishindmitriy.timetable.app.TimeTableApp;
import mishindmitriy.timetable.model.Pair;
import mishindmitriy.timetable.utils.Prefs;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import static mishindmitriy.timetable.app.shedule.ScheduleActivity.PAGES_COUNT;

/**
 * Created by mishindmitriy on 04.10.2016.
 */

public class DaysPagerAdapter extends PagerAdapter {
    private final Realm realm;
    @Inject
    protected Prefs prefs;
    private Stack<RecyclerView> viewStack = new Stack<>();
    private LocalDate startDate = LocalDate.now();
    private SparseArray<RecyclerView> activeViews = new SparseArray<>();
    private Map<RecyclerView, Subscription> resultsMap = new HashMap<>();

    public DaysPagerAdapter(Realm realm) {
        this.realm = realm;
        TimeTableApp.component().inject(this);
    }

    private LocalDate getLocalDate(int pos) {
        return startDate.plusDays(pos - PAGES_COUNT / 2);
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void notifyDataChanged() {
        for (int i = 0; i < activeViews.size(); i++) {
            int position = activeViews.keyAt(i);
            RecyclerView recyclerView = activeViews.get(position);
            unbindResults(recyclerView);
            bindResults(recyclerView, position);
        }
    }

    private void bindResults(final RecyclerView recyclerView, int position) {
        resultsMap.put(
                recyclerView,
                getRealmResults(position)
                        .subscribe(new Action1<RealmResults<Pair>>() {
                            @Override
                            public void call(RealmResults<Pair> pairs) {
                                ((PairAdapter) recyclerView.getAdapter()).setData(pairs);
                            }
                        })
        );
    }

    private Observable<RealmResults<Pair>> getRealmResults(int position) {
        if (realm.isClosed()) return Observable.empty();
        return realm.where(Pair.class)
                .equalTo("date", getLocalDate(position).toString())
                .equalTo("scheduleSubject.id", prefs.getSelectedThingId())
                .findAllSortedAsync("number")
                .asObservable();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        LocalDate localDate = getLocalDate(position);
        if (localDate.isEqual(LocalDate.now())) {
            return "Сегодня";
        } else if (localDate.isEqual(LocalDate.now().minusDays(1))) {
            return "Вчера";
        } else if (localDate.isEqual(LocalDate.now().plusDays(1))) {
            return "Завтра";
        } else {
            return localDate.toString("dd MMM EE");
        }
    }

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RecyclerView recyclerView;
        if (viewStack.size() > 0) {
            recyclerView = viewStack.pop();
        } else {
            recyclerView = new RecyclerView(container.getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
            recyclerView.setVerticalScrollBarEnabled(true);
            recyclerView.setAdapter(new PairAdapter());
        }
        bindResults(recyclerView, position);
        activeViews.put(position, recyclerView);
        container.addView(recyclerView);
        return recyclerView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        RecyclerView recyclerView = (RecyclerView) object;
        activeViews.remove(position);
        unbindResults(recyclerView);
        container.removeView(recyclerView);
        viewStack.push(recyclerView);
    }

    private void unbindResults(RecyclerView recyclerView) {
        if (resultsMap.containsKey(recyclerView)) {
            resultsMap.get(recyclerView).unsubscribe();
            resultsMap.remove(recyclerView);
        }
    }
}
