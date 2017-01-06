package mishindmitriy.timetable.app.schedulesubjects;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import mishindmitriy.timetable.model.ScheduleSubject;

/**
 * Created by mishindmitriy on 03.01.2017.
 */

public interface ScheduleSubjectsView extends MvpView {
    void hideRefreshing();

    void showRefreshing();

    void setData(List<ScheduleSubject> allSortedAsync);

    void startScheduleActivity();
}
