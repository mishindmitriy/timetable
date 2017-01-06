package mishindmitriy.timetable.app.shedule;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import org.joda.time.LocalDate;

import io.realm.RealmResults;
import mishindmitriy.timetable.model.ScheduleSubject;

/**
 * Created by mishindmitriy on 06.01.2017.
 */

public interface ScheduleView extends MvpView {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showCurrentSubjectTitle(String name);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showRefreshing();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void hideRefreshing();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setStartDate(LocalDate newDate);

    @StateStrategyType(SkipStrategy.class)
    void notifyPagerDateChanged();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setData(RealmResults<ScheduleSubject> scheduleSubjects);
}