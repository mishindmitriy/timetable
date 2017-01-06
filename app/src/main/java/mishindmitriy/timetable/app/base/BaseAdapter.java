package mishindmitriy.timetable.app.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;

/**
 * Created by mishindmitriy on 02.07.2016.
 */
public abstract class BaseAdapter<I, VH extends BaseViewHolder<I>>
        extends RecyclerView.Adapter<VH> {
    private final PublishSubject<List<I>> publishSubject;
    private OnItemClickListener<I> itemClickListener = null;
    private List<I> filteredList = new ArrayList<>();

    protected BaseAdapter(Observable<String> filterQueryObservable) {
        publishSubject = PublishSubject.create();
        Observable.combineLatest(
                filterQueryObservable.observeOn(AndroidSchedulers.mainThread()),
                publishSubject,
                new Func2<String, List<I>, List<I>>() {
                    @Override
                    public List<I> call(String filterPhrase, List<I> items) {
                        List<I> filteredList = new ArrayList<I>();
                        if (items != null) {
                            if (filterPhrase == null || filterPhrase.isEmpty()) {
                                filteredList.addAll(items);
                            } else {
                                for (I t : items) {
                                    if (contains(t, filterPhrase)) {
                                        filteredList.add(t);
                                    }
                                }
                            }
                        }
                        return filteredList;
                    }
                }
        )
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<I>>() {
                    @Override
                    public void call(List<I> filteredList) {
                        BaseAdapter.this.filteredList = filteredList;
                        notifyDataSetChanged();
                    }
                });
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

    public void setData(List<I> items) {
        publishSubject.onNext(items);
    }

    protected boolean contains(I t, String filterPhrase) {
        return true;
    }

    public interface OnItemClickListener<ITEM> {
        void onItemClick(ITEM item);
    }
}
