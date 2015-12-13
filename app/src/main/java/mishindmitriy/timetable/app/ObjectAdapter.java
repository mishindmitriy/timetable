package mishindmitriy.timetable.app;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishindmitriy on 24.11.2015.
 */
public abstract class ObjectAdapter<T> extends BaseAdapter {
    protected List<T> objects = new ArrayList<>();

    public ObjectAdapter(List<T> list) {
        if (list != null) this.objects = list;
    }

    public ObjectAdapter() {
    }

    public void setData(List<T> objects) {
        this.objects = objects;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (objects != null)
            return objects.size();
        return 0;
    }

    @Override
    public T getItem(int position) {
        if (objects != null)
            return objects.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (objects != null && objects.get(position) != null)
            return objects.get(position).hashCode();
        return 0;
    }
}
