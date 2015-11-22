package mishindmitriy.timetable.app;

import android.app.Application;
import android.os.StrictMode;

import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.model.db.DatabaseHelper;
import mishindmitriy.timetable.utils.AnalyticsTrackers;
import mishindmitriy.timetable.utils.PreferencesHelper;

/**
 * Created by mishindmitriy on 25.08.2015.
 * TimeTable Application Class
 */
public class TimeTableApp extends Application {

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
        AnalyticsTrackers.initialize(this);
        PreferencesHelper.init(getSharedPreferences(PreferencesHelper.APP_PREFERENCES, MODE_PRIVATE));
        super.onCreate();
        DatabaseHelper.init(getApplicationContext());
    }
}
