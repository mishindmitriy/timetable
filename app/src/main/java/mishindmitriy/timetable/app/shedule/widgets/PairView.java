package mishindmitriy.timetable.app.shedule.widgets;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.model.data.Pair;
import mishindmitriy.timetable.model.data.ThingType;
import mishindmitriy.timetable.utils.DateUtils;
import mishindmitriy.timetable.utils.ParseHelper;

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

    public PairView(Context context) {
        super(context);
    }

    public void setPair(ThingType thing, Pair pair) {
        switch (thing) {
            case GROUP:
                classroomTextView.setText(pair.getClassroom());
                subjectTextView.setText(pair.getSubject());
                teacherTextView.setText(pair.getPrepod());
                pairTypeTextView.setText(pair.getTypePair());
                break;
            case TEACHER:
                classroomTextView.setText(pair.getClassroom());
                subjectTextView.setText(pair.getSubject());
                teacherTextView.setText(pair.getGroups());
                pairTypeTextView.setText(pair.getTypePair());
                break;
            case CLASSROOM:
                classroomTextView.setText(pair.getGroups());
                subjectTextView.setText(pair.getSubject());
                teacherTextView.setText(pair.getPrepod());
                pairTypeTextView.setText(pair.getTypePair());
                break;
        }

        String dayOfWeek = DateUtils.getDayOfWeek(pair.getDate());
        if (dayOfWeek.equals("Суббота")) {   //время пар в субботу
            switch (pair.getPairNumber()) {
                case 1:
                    pairStartTextView.setText(ParseHelper.Saturday.firstPairStart);
                    pairEndTextView.setText(ParseHelper.Saturday.firstPairEnd);
                    break;
                case 2:
                    pairStartTextView.setText(ParseHelper.Saturday.secondPairStart);
                    pairEndTextView.setText(ParseHelper.Saturday.secondPairEnd);
                    break;
                case 3:
                    pairStartTextView.setText(ParseHelper.Saturday.thirdPairStart);
                    pairEndTextView.setText(ParseHelper.Saturday.thirdPairEnd);
                    break;
                case 4:
                    pairStartTextView.setText(ParseHelper.Saturday.fourthPairStart);
                    pairEndTextView.setText(ParseHelper.Saturday.fourthPairEnd);
                    break;
                case 5:
                    pairStartTextView.setText(ParseHelper.Saturday.fifthPairStart);
                    pairEndTextView.setText(ParseHelper.Saturday.fifthPairEnd);
                    break;
            }
        } else {
            switch (pair.getPairNumber()) {

                case 1:
                    pairStartTextView.setText(ParseHelper.firstPairStart);
                    pairEndTextView.setText(ParseHelper.firstPairEnd);
                    break;
                case 2:
                    pairStartTextView.setText(ParseHelper.secondPairStart);
                    pairEndTextView.setText(ParseHelper.secondPairEnd);
                    break;
                case 3:
                    pairStartTextView.setText(ParseHelper.thirdPairStart);
                    pairEndTextView.setText(ParseHelper.thirdPairEnd);
                    break;
                case 4:
                    pairStartTextView.setText(ParseHelper.fourthPairStart);
                    pairEndTextView.setText(ParseHelper.fourthPairEnd);
                    break;
                case 5:
                    pairStartTextView.setText(ParseHelper.fifthPairStart);
                    pairEndTextView.setText(ParseHelper.fifthPairEnd);
                    break;
                case 6:
                    pairStartTextView.setText(ParseHelper.sixthPairStart);
                    pairEndTextView.setText(ParseHelper.sixthPairEnd);
                    break;
                case 7:
                    pairStartTextView.setText(ParseHelper.seventhPairStart);
                    pairEndTextView.setText(ParseHelper.seventhPairEnd);
                    break;
            }
        }
    }
}
