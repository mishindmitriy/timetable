package mishindmitriy.timetable.app;

import android.app.Application;
import android.os.StrictMode;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.model.db.DatabaseHelper;
import mishindmitriy.timetable.utils.AnalyticsTrackers;

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
        DatabaseHelper.init(getApplicationContext());
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DatabaseHelper.getInstance().close();
        OpenHelperManager.releaseHelper();
    }
}
