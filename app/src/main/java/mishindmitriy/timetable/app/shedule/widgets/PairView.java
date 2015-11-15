package mishindmitriy.timetable.app.shedule.widgets;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.model.data.Pair;
import mishindmitriy.timetable.model.data.PairsTimeConverter;
import mishindmitriy.timetable.model.data.ThingType;
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

    public void setPair(ThingType thing, Pair pair) {
        switch (thing) {
            case GROUP:
                classroomTextView.setText(pair.getClassroom());
                teacherTextView.setText(pair.getTeacher());
                break;
            case TEACHER:
                classroomTextView.setText(pair.getClassroom());
                teacherTextView.setText(pair.getGroups());
                break;
            case CLASSROOM:
                classroomTextView.setText(pair.getGroups());
                teacherTextView.setText(pair.getTeacher());
                break;
        }

        pairTypeTextView.setText(pair.getType());
        subjectTextView.setText(pair.getSubject());
        noteTextView.setText(pair.getNote());
        pairNumberTextView.setText(String.valueOf(pair.getNumber()));

        boolean isSaturday=DateUtils.getDayOfWeek(pair.getDate()).equals("Суббота");
        pairStartTextView.setText(PairsTimeConverter.getPairStartTime(pair.getNumber(),isSaturday));
        pairEndTextView.setText(PairsTimeConverter.getPairEndTime(pair.getNumber(),isSaturday));
    }
}
