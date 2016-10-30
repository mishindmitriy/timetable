package mishindmitriy.timetable.app.shedule;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.Stack;

import io.realm.Realm;
import mishindmitriy.timetable.model.Pair;
import mishindmitriy.timetable.utils.Prefs;

import static mishindmitriy.timetable.app.shedule.SheduleActivity.PAGES_COUNT;

/**
 * Created by mishindmitriy on 04.10.2016.
 */

public class DaysPagerAdapter extends PagerAdapter {
    private final Realm realm;
    private Stack<RecyclerView> viewStack = new Stack<>();
    private LocalDate startDate = LocalDate.now();

    public DaysPagerAdapter(Realm realm) {
        this.realm = realm;
    }

    private LocalDate getLocalDate(int pos) {
        return startDate.plusDays(pos - PAGES_COUNT / 2);
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        notifyDataSetChanged();
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
        ((PairAdapter) recyclerView.getAdapter())
                .setData(realm.where(Pair.class)
                        .beginGroup()
                        .equalTo("date", getLocalDate(position).toString())
                        .equalTo("thing.id", Prefs.get().getSelectedThingId())
                        .endGroup()
                        .findAllSortedAsync("number"));
        container.addView(recyclerView);
        return recyclerView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view != null && view instanceof RecyclerView) {
                if (object.equals(view)) {
                    RecyclerView recyclerView = (RecyclerView) view;
                    ((PairAdapter) recyclerView.getAdapter()).setItems(null);
                    container.removeView(view);
                    viewStack.push(recyclerView);
                    break;
                }
            }
        }
    }
}
