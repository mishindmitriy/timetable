package mishindmitriy.timetable.app;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import mishindmitriy.timetable.app.schedulesubjects.ScheduleSubjectsPresenter;
import mishindmitriy.timetable.app.shedule.DaysPagerAdapter;
import mishindmitriy.timetable.app.shedule.SchedulePresenter;
import mishindmitriy.timetable.utils.Prefs;

/**
 * Created by mishindmitriy on 03.01.2017.
 */

@Component(modules = {AndroidModule.class, Prefs.SettingsModule.class,
        RealmModule.class})
@Singleton
public interface AppComponent {
    @ApplicationContext
    Context context();

    void inject(DaysPagerAdapter daysPagerAdapter);

    void inject(SplashScreen splashScreen);

    void inject(SchedulePresenter schedulePresenter);

    void inject(ScheduleSubjectsPresenter scheduleSubjectsPresenter);

    void inject(TimeTableApp timeTableApp);
}
