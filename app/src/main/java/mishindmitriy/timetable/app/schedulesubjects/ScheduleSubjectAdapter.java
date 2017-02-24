package mishindmitriy.timetable.app.schedulesubjects;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseAdapter;
import mishindmitriy.timetable.app.base.BaseViewHolder;
import mishindmitriy.timetable.databinding.ItemThingBinding;
import mishindmitriy.timetable.model.ScheduleSubject;

/**
 * Created by dmitriy on 19.09.16.
 */
public class ScheduleSubjectAdapter extends BaseAdapter<ScheduleSubject, ScheduleSubjectViewHolder>
        implements StickyRecyclerHeadersAdapter<BaseViewHolder<String>> {
    public ScheduleSubjectAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public ScheduleSubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScheduleSubjectViewHolder(parent);
    }

    @Override
    public long getHeaderId(int position) {
        switch (getItem(position).getEnumType()) {
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
        final ItemThingBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_thing,
                parent, false);
        binding.text.setBackgroundColor(parent.getContext().getResources().getColor(R.color.teal500));
        binding.text.setTextColor(Color.WHITE);
        return new BaseViewHolder<String>(binding.getRoot()) {
            @Override
            public void update(@NonNull String item) {
                binding.text.setText(item);
            }
        };
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder<String> holder, int position) {
        switch (getItem(position).getEnumType()) {
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
    protected boolean contains(ScheduleSubject s, String filterPhrase) {
        return s.getName().toLowerCase().contains(filterPhrase.toLowerCase());
    }
}
