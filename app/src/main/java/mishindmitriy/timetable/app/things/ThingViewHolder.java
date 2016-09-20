package mishindmitriy.timetable.app.things;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseViewHolder;
import mishindmitriy.timetable.model.Thing;

/**
 * Created by dmitriy on 19.09.16.
 */
public class ThingViewHolder extends BaseViewHolder<Thing> {
    public ThingViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thing, parent, false));
    }

    @Override
    public void update(@NonNull Thing item) {
        ((TextView) itemView.findViewById(R.id.text)).setText(item.getName());
    }
}
