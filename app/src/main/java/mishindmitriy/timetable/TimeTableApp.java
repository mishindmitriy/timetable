package mishindmitriy.timetable;
import android.app.Application;
import android.os.StrictMode;

/**
 * Created by mishindmitriy on 25.08.2015.
 * TimeTable Application Class
 */
public class TimeTableApp extends Application {
    private final static boolean DEVELOPER_MODE=true;
    @Override
    public void onCreate() {
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        AnalyticsTrackers.initialize(this);
        super.onCreate();
    }
}