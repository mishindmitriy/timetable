package mishindmitriy.timetable.app.schedulesubjects;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseViewHolder;
import mishindmitriy.timetable.model.ScheduleSubject;

/**
 * Created by dmitriy on 19.09.16.
 */
public class ScheduleSubjectViewHolder extends BaseViewHolder<ScheduleSubject> {
    public ScheduleSubjectViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thing, parent, false));
    }

    @Override
    public void update(@NonNull ScheduleSubject item) {
        ((TextView) itemView.findViewById(R.id.text)).setText(item.getName());
    }
}
