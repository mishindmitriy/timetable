package mishindmitriy.timetable.model;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import mishindmitriy.timetable.app.shedule.widgets.PairHeaderView;
import mishindmitriy.timetable.app.shedule.widgets.PairHeaderView_;
import mishindmitriy.timetable.app.shedule.widgets.PairView;
import mishindmitriy.timetable.app.shedule.widgets.PairView_;
import mishindmitriy.timetable.model.data.Pair;
import mishindmitriy.timetable.model.data.ThingType;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by mishindmitriy on 12.09.2015.
 * Adapter for shedule output in ListView
 */
public class SheduleListAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private final Activity mContext;
    private List<Pair> shedule;
    private ThingType mWhatThing;

    public SheduleListAdapter(Activity context, List<Pair> shedule, ThingType whatThing) {
        this.mContext = context;
        this.shedule = shedule;
        this.mWhatThing = whatThing;
    }

    public void setData(List<Pair> shedule, ThingType thing) {
        this.shedule = shedule;
        this.mWhatThing=thing;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.shedule.size();
    }

    @Override
    public Object getItem(int position) {
        return this.shedule.get(position);
    }

    @Override
    public long getItemId(int position) {
        return shedule.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PairView pairView=(PairView) convertView;
        if (pairView==null) {
            pairView = PairView_.build(mContext);
        }
        pairView.setPair(mWhatThing,shedule.get(position));
        return pairView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        PairHeaderView headerView=PairHeaderView_.build(mContext);
        headerView.setDate(this.shedule.get(position).getDate());
        return headerView;
    }

    @Override
    public long getHeaderId(int position) {
        return this.shedule.get(position).getDate().hashCode();
    }
}