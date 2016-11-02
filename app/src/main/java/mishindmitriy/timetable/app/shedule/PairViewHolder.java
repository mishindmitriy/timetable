package mishindmitriy.timetable.app.shedule;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseViewHolder;
import mishindmitriy.timetable.model.Pair;

/**
 * Created by dmitriy on 19.09.16.
 */
public class PairViewHolder extends BaseViewHolder<Pair> {
    private final TextView classroomTextView;
    private final TextView subjectTextView;
    private final TextView teacherTextView;
    private final TextView pairTypeTextView;
    private final TextView pairStartTextView;
    private final TextView pairEndTextView;
    private final TextView noteTextView;
    private final TextView pairNumberTextView;
    private final View background;

    public PairViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pair, parent, false));
        classroomTextView = (TextView) itemView.findViewById(R.id.textViewClassroom);
        subjectTextView = (TextView) itemView.findViewById(R.id.textViewSubject);
        teacherTextView = (TextView) itemView.findViewById(R.id.textViewTeacher);
        pairTypeTextView = (TextView) itemView.findViewById(R.id.textViewTypePair);
        pairStartTextView = (TextView) itemView.findViewById(R.id.textViewPairStart);
        pairEndTextView = (TextView) itemView.findViewById(R.id.textViewPairEnd);
        noteTextView = (TextView) itemView.findViewById(R.id.textNote);
        pairNumberTextView = (TextView) itemView.findViewById(R.id.textViewPairNumber);
        background = itemView.findViewById(R.id.background);

        subjectTextView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        teacherTextView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        pairTypeTextView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        Drawable drawable = classroomTextView.getCompoundDrawables()[0];
        if (drawable != null) drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        drawable = noteTextView.getCompoundDrawables()[0];
        if (drawable != null) drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void update(@NonNull Pair pair) {
        switch (pair.getScheduleSubject().getEnumType()) {
            case GROUP:
                classroomTextView.setText(pair.getClassroom());
                teacherTextView.setText(pair.getTeacher());
                Drawable drawable = itemView.getContext()
                        .getResources().getDrawable(R.drawable.ic_record_voice_over_white_18dp);
                if (drawable != null) drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                teacherTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                break;
            case TEACHER:
                classroomTextView.setText(pair.getClassroom());
                teacherTextView.setText(pair.getGroup());
                teacherTextView.setCompoundDrawables(null, null, null, null);
                break;
            case CLASSROOM:
                classroomTextView.setText(pair.getGroup());
                teacherTextView.setText(pair.getTeacher());
                teacherTextView.setCompoundDrawables(null, null, null, null);
                break;
        }

        if (TextUtils.isEmpty(pair.getTeacher())) {
            teacherTextView.setVisibility(View.GONE);
        } else teacherTextView.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(pair.getNote())) {
            noteTextView.setVisibility(View.GONE);
        } else noteTextView.setVisibility(View.VISIBLE);

        pairTypeTextView.setText(pair.getType());
        subjectTextView.setText(pair.getSubject());
        noteTextView.setText(pair.getNote());
        pairNumberTextView.setText(String.valueOf(pair.getNumber()));

        pairStartTextView.setText(pair.getStringStartTime());
        pairEndTextView.setText(pair.getStringEndTime());

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
            pairStartTextView.setVisibility(View.INVISIBLE);
        } else {
            pairStartTextView.setVisibility(View.VISIBLE);
        }

        if (endDateTime.isBefore(now)) {
            pairEndTextView.setVisibility(View.INVISIBLE);
        } else {
            pairEndTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setBackgroundHeight(int height) {
        background.getLayoutParams().height = height;
        background.requestLayout();
    }
}
