package mishindmitriy.timetable.app;

import android.app.Application;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.model.db.HelperFactory;

/**
 * Created by mishindmitriy on 25.08.2015.
 * TimeTable Application Class
 */
public class TimeTableApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
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
        HelperFactory.init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        HelperFactory.release();
        super.onTerminate();
    }
}
