package mishindmitriy.timetable.app.schedulesubjects;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

/**
 * Created by mishindmitriy on 03.01.2017.
 */
@InjectViewState
public class ScheduleSubjectsPresenter extends MvpPresenter<ScheduleSubjectsView> {
    @Override
    protected void onFirstViewAttach() {
        getViewState().showSubjects();
    }
}
