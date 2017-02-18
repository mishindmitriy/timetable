package mishindmitriy.timetable.app.schedulesubjects;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import mishindmitriy.timetable.model.ScheduleSubject;

/**
 * Created by mishindmitriy on 03.01.2017.
 */

public interface ScheduleSubjectsView extends MvpView {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void setRefreshing(boolean enable);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setData(List<ScheduleSubject> allSortedAsync);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void startScheduleActivity();
}
