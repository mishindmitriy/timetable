package mishindmitriy.timetable.app.shedule.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.model.data.PairsTimeConverter;
import mishindmitriy.timetable.model.data.entity.Pair;
import mishindmitriy.timetable.utils.DateUtils;

/**
 * Created by mishindmitriy on 13.11.2015.
 */
@EViewGroup(R.layout.item_pair)
public class PairView extends RelativeLayout {
    @ViewById(R.id.textViewClassroom)
    protected TextView classroomTextView;
    @ViewById(R.id.textViewSubject)
    protected TextView subjectTextView;
    @ViewById(R.id.textViewTeacher)
    protected TextView teacherTextView;
    @ViewById(R.id.textViewTypePair)
    protected TextView pairTypeTextView;
    @ViewById(R.id.textViewPairStart)
    protected TextView pairStartTextView;
    @ViewById(R.id.textViewPairEnd)
    protected TextView pairEndTextView;
    @ViewById(R.id.textNote)
    protected TextView noteTextView;
    @ViewById(R.id.textViewPairNumber)
    protected TextView pairNumberTextView;

    public PairView(Context context) {
        super(context);
    }

    @AfterViews
    void init()
    {
        subjectTextView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        teacherTextView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        pairTypeTextView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        Drawable drawable = classroomTextView.getCompoundDrawables()[0];
        if (drawable != null) drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        drawable = noteTextView.getCompoundDrawables()[0];
        if (drawable != null) drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
    }

    public void setPair(Pair pair) {
        switch (pair.getThing().getType()) {
            case GROUP:
                classroomTextView.setText(pair.getClassroom());
                teacherTextView.setText(pair.getTeacher());
                Drawable drawable = getResources().getDrawable(R.drawable.ic_face_white_18dp);
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

        if (TextUtils.isEmpty(pair.getTeacher()))
        {
            teacherTextView.setVisibility(GONE);
        } else teacherTextView.setVisibility(VISIBLE);

        if (TextUtils.isEmpty(pair.getNote())) {
            noteTextView.setVisibility(GONE);
        } else noteTextView.setVisibility(VISIBLE);

        pairTypeTextView.setText(pair.getType());
        subjectTextView.setText(pair.getSubject());
        noteTextView.setText(pair.getNote());
        pairNumberTextView.setText(String.valueOf(pair.getNumber()));

        boolean isSaturday = DateUtils.getDayOfWeek(pair.getDate()).equalsIgnoreCase("Суббота");
        pairStartTextView.setText(PairsTimeConverter.getPairStartTime(pair.getNumber(),isSaturday));
        pairEndTextView.setText(PairsTimeConverter.getPairEndTime(pair.getNumber(),isSaturday));
    }
}
