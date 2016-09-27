package mishindmitriy.timetable.app;

import android.app.Application;
import android.content.Intent;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.utils.Prefs;

/**
 * Created by mishindmitriy on 19.09.2016.
 * TimeTable Application Class
 */
public class TimeTableApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        } else {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build());
        Prefs.init(this);
        startService(new Intent(this, NotificationService.class));
    }
}
