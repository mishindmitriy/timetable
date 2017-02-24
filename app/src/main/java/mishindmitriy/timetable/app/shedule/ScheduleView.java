package mishindmitriy.timetable.app.shedule;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import org.joda.time.LocalDate;

import java.util.List;

import mishindmitriy.timetable.model.ScheduleSubject;

/**
 * Created by mishindmitriy on 06.01.2017.
 */

public interface ScheduleView extends MvpView {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showCurrentSubjectTitle(String name);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setStartDate(LocalDate newDate);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void notifyPagerDateChanged();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setSubjectsData(List<ScheduleSubject> scheduleSubjects);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setRefreshing(boolean enable);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showDateDialog();

    void showFeedbackAlert();

    void dismissDateDialog();
}
