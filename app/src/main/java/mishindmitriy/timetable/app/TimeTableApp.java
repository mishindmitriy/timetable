package mishindmitriy.timetable.app;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.log.RealmLog;
import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.utils.DataHelper;
import mishindmitriy.timetable.utils.Prefs;

/**
 * Created by mishindmitriy on 19.09.2016.
 * TimeTable Application Class
 */
public class TimeTableApp extends Application {
    private static AppComponent appComponent;

    public static AppComponent component() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        JodaTimeAndroid.init(this);
        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build());

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        } else {
            RealmLog.setLevel(Log.ERROR);
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }

        DataHelper.init(this);

        appComponent = DaggerAppComponent.builder()
                .androidModule(new AndroidModule(this))
                .realmModule(new RealmModule())
                .settingsModule(new Prefs.SettingsModule())
                .firebaseModule(new FirebaseModule())
                .build();
        //startService(new Intent(this, NotificationService.class));
    }

    @Module
    static class FirebaseModule {
        @Provides
        @Singleton
        public Firebase provideFirebase() {
            return new Firebase("https://timetable-cbbbf.firebaseio.com/");
        }
    }
}
