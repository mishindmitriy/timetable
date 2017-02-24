package mishindmitriy.timetable.app.schedulesubjects;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseViewHolder;
import mishindmitriy.timetable.databinding.ItemThingBinding;
import mishindmitriy.timetable.model.ScheduleSubject;

/**
 * Created by dmitriy on 19.09.16.
 */
public class ScheduleSubjectViewHolder extends BaseViewHolder<ScheduleSubject> {
    private final ItemThingBinding binding;

    public ScheduleSubjectViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thing, parent, false));
        binding = DataBindingUtil.bind(itemView);
    }

    @Override
    public void update(@NonNull ScheduleSubject item) {
        binding.text.setText(item.getName());
    }
}
