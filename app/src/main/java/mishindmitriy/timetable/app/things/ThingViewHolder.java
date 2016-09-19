package mishindmitriy.timetable.app.things;

import android.support.annotation.NonNull;
import android.view.View;

import mishindmitriy.timetable.app.base.BaseViewHolder;
import mishindmitriy.timetable.app.things.widget.ViewItemThing;
import mishindmitriy.timetable.model.Thing;

/**
 * Created by dmitriy on 19.09.16.
 */
public class ThingViewHolder extends BaseViewHolder<Thing> {
    public ThingViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void update(@NonNull Thing item) {
        ((ViewItemThing) itemView).setThing(item);
    }
}
