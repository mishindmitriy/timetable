package mishindmitriy.timetable.app.shedule;

import android.view.ViewGroup;

import mishindmitriy.timetable.app.base.BaseAdapter;
import mishindmitriy.timetable.model.Pair;

/**
 * Created by dmitriy on 19.09.16.
 */
public class PairAdapter extends BaseAdapter<Pair, PairViewHolder> {
    public PairAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public PairViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PairViewHolder(parent);
    }
}
