package mishindmitriy.timetable.app.shedule.widgets;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.joda.time.LocalDate;

import mishindmitriy.timetable.R;

/**
 * Created by mishindmitriy on 13.11.2015.
 */
@EViewGroup(R.layout.header_pair)
public class HeaderPairView extends RelativeLayout {
    @ViewById(R.id.day_of_week)
    protected TextView dayOfWeekTextView;
    @ViewById(R.id.date)
    protected TextView dateTextView;
    @ViewById(R.id.layout)
    protected RelativeLayout layout;

    public HeaderPairView(Context context) {
        super(context);
    }

    public void setDate(LocalDate date) {
        dayOfWeekTextView.setText(date.toString("E"));
        dateTextView.setText(date.toString("dd.MM.yyyy"));
        if (LocalDate.now().isEqual(date)) {
            layout.setBackgroundColor(getResources().getColor(R.color.select));
        }
    }
}
