package mishindmitriy.timetable.app.shedule;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import mishindmitriy.timetable.app.shedule.widgets.HeaderPairView;
import mishindmitriy.timetable.app.shedule.widgets.HeaderPairView_;
import mishindmitriy.timetable.app.shedule.widgets.PairView;
import mishindmitriy.timetable.app.shedule.widgets.PairView_;
import mishindmitriy.timetable.model.data.entity.Pair;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by mishindmitriy on 12.09.2015.
 * Adapter for shedule output in ListView
 */
public class SheduleListAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private final Activity mContext;
    private List<Pair> shedule;

    public SheduleListAdapter(Activity context, List<Pair> shedule) {
        this.mContext = context;
        this.shedule = shedule;
    }

    public void setData(List<Pair> shedule) {
        this.shedule = shedule;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (shedule==null) return 0;
        return this.shedule.size();
    }

    @Override
    public Object getItem(int position) {
        return this.shedule.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (shedule!=null && shedule.size()>0) return shedule.get(position).hashCode();
        else return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PairView pairView=(PairView) convertView;
        if (pairView==null) {
            pairView = PairView_.build(mContext);
        }
        pairView.setPair(shedule.get(position));
        return pairView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderPairView headerView = HeaderPairView_.build(mContext);
        headerView.setDate(this.shedule.get(position).getDate());
        return headerView;
    }

    @Override
    public long getHeaderId(int position) {
        return this.shedule.get(position).getDate().hashCode();
    }
}
