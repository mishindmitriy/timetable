package mishindmitriy.timetable.app.shedule.widgets;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.utils.ParseHelper;

/**
 * Created by mishindmitriy on 13.11.2015.
 */
@EViewGroup(R.layout.header_pair)
public class PairHeaderView extends RelativeLayout {
    @ViewById(R.id.day_of_week)
    protected TextView dayOfWeekTextView;
    @ViewById(R.id.today)
    protected TextView todayTextView;
    @ViewById(R.id.date)
    protected TextView dateTextView;

    public PairHeaderView(Context context) {
        super(context);
    }

    public void setDate(String stringDate) {
        dateTextView.setText(stringDate);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(ParseHelper.formatDate);
        try {
            date = sdf.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dayOfWeek = ParseHelper.getDayOfWeek(date);

        String today = sdf.format(new Date());
        String someDate = sdf.format(date);
        if (today.equals(someDate)) {
            todayTextView.setVisibility(View.VISIBLE);
        } else todayTextView.setVisibility(View.GONE);

        dayOfWeekTextView.setText(dayOfWeek);
    }
}
