package mishindmitriy.timetable.app.things;

import android.view.ViewGroup;

import io.realm.Case;
import io.realm.Realm;
import io.realm.Sort;
import mishindmitriy.timetable.app.base.BaseAdapter;
import mishindmitriy.timetable.model.Thing;

/**
 * Created by dmitriy on 19.09.16.
 */
public class ThingAdapter extends BaseAdapter<Thing, ThingViewHolder> {
    public ThingAdapter() {
        setHasStableIds(true);
        loadAll();
    }

    private void loadAll() {
        Realm realm = Realm.getDefaultInstance();
        setData(realm.where(Thing.class)
                .findAllSortedAsync("rating", Sort.ASCENDING, "name", Sort.ASCENDING));
        realm.close();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public ThingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ThingViewHolder(parent);
    }

    public void filter(String phrase) {
        if (phrase == null || phrase.isEmpty()) {
            loadAll();
            return;
        }
        Realm realm = Realm.getDefaultInstance();
        setData(realm.where(Thing.class)
                .contains("name", phrase, Case.INSENSITIVE)
                .findAllSortedAsync("rating", Sort.ASCENDING, "name", Sort.ASCENDING));
        // TODO: 20.09.2016 change this logic to new list and manual filter
        realm.close();
    }
}
