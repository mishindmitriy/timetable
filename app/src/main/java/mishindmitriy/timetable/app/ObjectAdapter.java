package mishindmitriy.timetable.app;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishindmitriy on 24.11.2015.
 */
public abstract class ObjectAdapter<T> extends BaseAdapter {
    private List<T> list = new ArrayList<>();

    public ObjectAdapter(List<T> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    @Override
    public T getItem(int position) {
        if (list != null)
            return list.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (list != null && list.get(position) != null)
            return list.get(position).hashCode();
        return 0;
    }
}
