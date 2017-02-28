package mishindmitriy.timetable.app.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishindmitriy on 02.07.2016.
 */
public abstract class BaseAdapter<I, VH extends BaseViewHolder<I>>
        extends RecyclerView.Adapter<VH> {
    private OnItemClickListener<I> itemClickListener = null;
    private List<I> data = new ArrayList<>();

    @Override
    public int getItemCount() {
        return data.size();
    }

    public I getItem(int position) {
        return position < 0 || position > getItemCount() - 1 ? null : data.get(position);
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    I item = getItem(holder.getAdapterPosition());
                    if (item != null) itemClickListener.onItemClick(item);
                }
            }
        });
        holder.update(getItem(position));
    }

    public void setOnItemClickListener(OnItemClickListener<I> itemOnClickListener) {
        this.itemClickListener = itemOnClickListener;
    }

    public void setData(List<I> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener<ITEM> {
        void onItemClick(ITEM item);
    }
}
