package mishindmitriy.timetable.app.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by mishindmitriy on 02.07.2016.
 */
public abstract class BaseViewHolder<I> extends RecyclerView.ViewHolder {
    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void update(@NonNull I item);
}
