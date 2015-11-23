package mishindmitriy.timetable.app.shedule.widgets;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Date;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.model.data.DateFormatter;
import mishindmitriy.timetable.utils.DateUtils;

/**
 * Created by mishindmitriy on 13.11.2015.
 */
@EViewGroup(R.layout.header_pair)
public class PairHeaderView extends RelativeLayout {
    @ViewById(R.id.day_of_week)
    protected TextView dayOfWeekTextView;
    @ViewById(R.id.date)
    protected TextView dateTextView;
    @ViewById(R.id.layout)
    protected RelativeLayout layout;

    public PairHeaderView(Context context) {
        super(context);
    }

    public void setDate(Date date) {
        String dayOfWeek = DateUtils.getDayOfWeek(date);
        String today = DateFormatter.DateToString(new Date());
        dayOfWeekTextView.setText(dayOfWeek);
        dateTextView.setText(DateFormatter.DateToString(date));
        if (today.equals(DateFormatter.DateToString(date))) {
            layout.setBackgroundColor(getResources().getColor(R.color.select));
        }
    }
}
