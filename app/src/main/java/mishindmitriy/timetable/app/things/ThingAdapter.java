package mishindmitriy.timetable.app.things;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseAdapter;
import mishindmitriy.timetable.app.base.BaseViewHolder;
import mishindmitriy.timetable.model.Thing;

/**
 * Created by dmitriy on 19.09.16.
 */
public class ThingAdapter extends BaseAdapter<Thing, ThingViewHolder>
        implements StickyRecyclerHeadersAdapter<BaseViewHolder<String>> {

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public ThingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ThingViewHolder(parent);
    }

    @Override
    public long getHeaderId(int position) {
        switch (getItem(position).getType()) {
            case CLASSROOM:
                return 1;
            case TEACHER:
                return 2;
            case GROUP:
                return 3;
            default:
                return 0;
        }
    }

    @Override
    public BaseViewHolder<String> onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_thing, parent, false);
        ((TextView) view.findViewById(R.id.text))
                .setBackgroundColor(view.getContext().getResources()
                        .getColor(R.color.teal500));
        ((TextView) view.findViewById(R.id.text)).setTextColor(Color.WHITE);
        return new BaseViewHolder<String>(view) {
            @Override
            public void update(@NonNull String item) {
                ((TextView) itemView.findViewById(R.id.text)).setText(item);
            }
        };
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder<String> holder, int position) {
        switch (getItem(position).getType()) {
            case CLASSROOM:
                holder.update(holder.itemView.getContext().getResources()
                        .getString(R.string.classrooms));
                break;
            case TEACHER:
                holder.update(holder.itemView.getContext().getResources()
                        .getString(R.string.teachers));
                break;
            case GROUP:
                holder.update(holder.itemView.getContext().getResources()
                        .getString(R.string.groups));
                break;
            default:
                holder.update("");
                break;
        }
    }

    @Override
    protected boolean contains(Thing t, String filterPhrase) {
        return t.getName().toLowerCase().contains(filterPhrase.toLowerCase());
    }
}
