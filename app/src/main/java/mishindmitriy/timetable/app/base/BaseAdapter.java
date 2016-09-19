package mishindmitriy.timetable.app.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by mishindmitriy on 02.07.2016.
 */
public abstract class BaseAdapter<I extends RealmObject, VH extends BaseViewHolder<I>>
        extends RecyclerView.Adapter<VH> {
    private RealmResults<I> items;
    private OnItemClickListener<I> itemClickListener = null;

    public RealmResults<I> getItems() {
        return items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public I getItem(int position) {
        return items.get(position);
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(getItem(holder.getAdapterPosition()));
                }
            }
        });
        holder.update(getItem(position));
    }

    public void setOnItemClickListener(OnItemClickListener<I> itemOnClickListener) {
        this.itemClickListener = itemOnClickListener;
    }

    public List<I> getData() {
        return items;
    }

    public void setData(RealmResults<I> items) {
        this.items = items;
        items.addChangeListener(new RealmChangeListener<RealmResults<I>>() {
            @Override
            public void onChange(RealmResults<I> element) {
                notifyDataSetChanged();
            }
        });
        notifyDataSetChanged();
    }

    public interface OnItemClickListener<ITEM> {
        void onItemClick(ITEM item);
    }
}
