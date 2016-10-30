package mishindmitriy.timetable.app.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
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
    private List<I> filteredList = new ArrayList<>();
    private String filterPhrase = null;
    private RealmChangeListener<RealmResults<I>> changeListener = new RealmChangeListener<RealmResults<I>>() {
        @Override
        public void onChange(RealmResults<I> element) {
            filterAndNotifyDataChanged();
        }
    };

    public RealmResults<I> getItems() {
        return items;
    }

    public void setItems(RealmResults<I> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public I getItem(int position) {
        return filteredList.get(position);
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

    public void setData(RealmResults<I> items) {
        if (this.items != null) {
            this.items.removeChangeListener(changeListener);
        }
        this.items = items;
        if (items == null) return;
        items.addChangeListener(changeListener);
        filterAndNotifyDataChanged();
    }

    private void filterAndNotifyDataChanged() {
        filteredList.clear();
        if (items == null) return;
        if (filterPhrase == null || filterPhrase.isEmpty()) {
            filteredList.addAll(items);
        } else {
            for (I t : items) {
                if (contains(t, filterPhrase)) {
                    filteredList.add(t);
                }
            }
        }
        notifyDataSetChanged();
    }

    protected boolean contains(I t, String filterPhrase) {
        return true;
    }

    public void filter(String phrase) {
        filterPhrase = phrase;
        filterAndNotifyDataChanged();
    }

    public interface OnItemClickListener<ITEM> {
        void onItemClick(ITEM item);
    }
}
