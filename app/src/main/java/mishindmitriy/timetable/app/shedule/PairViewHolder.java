package mishindmitriy.timetable.app.shedule;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseViewHolder;
import mishindmitriy.timetable.databinding.ItemPairBinding;
import mishindmitriy.timetable.model.Pair;

/**
 * Created by dmitriy on 19.09.16.
 */
public class PairViewHolder extends BaseViewHolder<Pair> {
    private final ItemPairBinding binding;

    public PairViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pair, parent, false));
        binding = DataBindingUtil.bind(itemView);
        binding.textViewSubject.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        binding.textViewTeacher.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        binding.textViewTypePair.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        Drawable drawable = binding.textViewClassroom.getCompoundDrawables()[0];
        if (drawable != null) drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        drawable = binding.textNote.getCompoundDrawables()[0];
        if (drawable != null) drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void update(@NonNull Pair pair) {
        switch (pair.getScheduleSubject().getEnumType()) {
            case GROUP:
                binding.textViewClassroom.setText(pair.getClassroom());
                binding.textViewTeacher.setText(pair.getTeacher());
                Drawable drawable = itemView.getContext()
                        .getResources().getDrawable(R.drawable.ic_record_voice_over_white_18dp);
                if (drawable != null) drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                binding.textViewTeacher.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                break;
            case TEACHER:
                binding.textViewClassroom.setText(pair.getClassroom());
                binding.textViewTeacher.setText(pair.getGroup());
                binding.textViewTeacher.setCompoundDrawables(null, null, null, null);
                break;
            case CLASSROOM:
                binding.textViewClassroom.setText(pair.getGroup());
                binding.textViewTeacher.setText(pair.getTeacher());
                binding.textViewTeacher.setCompoundDrawables(null, null, null, null);
                break;
        }

        if (TextUtils.isEmpty(pair.getTeacher())) {
            binding.textViewTeacher.setVisibility(View.GONE);
        } else binding.textViewTeacher.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(pair.getNote())) {
            binding.textNote.setVisibility(View.GONE);
        } else binding.textNote.setVisibility(View.VISIBLE);

        binding.textViewTypePair.setText(pair.getType());
        binding.textViewSubject.setText(pair.getSubject());
        binding.textNote.setText(pair.getNote());
        binding.textViewPairNumber.setText(String.valueOf(pair.getNumber()));

        binding.textViewPairStart.setText(pair.getStringStartTime());
        binding.textViewPairEnd.setText(pair.getStringEndTime());

        final DateTime startDateTime = pair.getStartDateTime();
        final DateTime endDateTime = pair.getEndDateTime();
        final DateTime now = DateTime.now();

        if (now.isAfter(startDateTime) && now.isBefore(endDateTime)) {
            itemView.post(new Runnable() {
                @Override
                public void run() {
                    final double pairMs = endDateTime.getMillis() - startDateTime.getMillis();
                    final double nowMsFromStart = now.getMillis() - startDateTime.getMillis();
                    setBackgroundHeight((int) Math.round(
                            itemView.getMeasuredHeight() / pairMs * nowMsFromStart
                    ));
                }
            });
        } else {
            if (LocalDate.now().equals(pair.getDate()) && endDateTime.isBefore(now)) {
                itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        setBackgroundHeight(itemView.getMeasuredHeight());
                    }
                });
            } else {
                setBackgroundHeight(0);
            }
        }

        if (startDateTime.isBefore(now)) {
            binding.textViewPairStart.setVisibility(View.INVISIBLE);
        } else {
            binding.textViewPairStart.setVisibility(View.VISIBLE);
        }

        if (endDateTime.isBefore(now)) {
            binding.textViewPairEnd.setVisibility(View.INVISIBLE);
        } else {
            binding.textViewPairEnd.setVisibility(View.VISIBLE);
        }
    }

    private void setBackgroundHeight(int height) {
        binding.background.getLayoutParams().height = height;
        binding.background.requestLayout();
    }
}
