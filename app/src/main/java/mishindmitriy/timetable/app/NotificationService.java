package mishindmitriy.timetable.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Stack;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.model.Pair;
import mishindmitriy.timetable.utils.Prefs;

/**
 * Created by mishindmitriy on 27.09.2016.
 */

public class NotificationService extends Service {
    private final Realm realm = Realm.getDefaultInstance();
    private AlarmManager alarmManager;
    private Stack<PendingIntent> pendingIntentStack = new Stack<>();
    private RealmResults<Pair> todayPairs = getTodayPairs();
    private final BroadcastReceiver timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED)
                    || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                    || action.equals(Intent.ACTION_DATE_CHANGED)
                    || action.equals(Intent.ACTION_TIME_TICK)) {
                if (BuildConfig.DEBUG) {
                    Log.d("testtt", "time changed");
                }
                updateNotifications(true);
            }
        }
    };
    private SharedPreferences.OnSharedPreferenceChangeListener listener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Prefs.KEY_SELECTED_THING_ID)) {
                updateNotifications(true);
            } else if (key.equals(Prefs.KEY_NOTIFICATIONS)
                    && !Prefs.get().isNotificationsEnabled()) {
                removeNotifications();
                stopSelf();
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateNotifications(false);
    }

    @Override
    public void onCreate() {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        super.onCreate();
        Prefs.get().register(listener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(timeChangedReceiver, intentFilter);
        updateNotifications(true);
    }

    private RealmResults<Pair> getTodayPairs() {
        if (BuildConfig.DEBUG) {
            Log.d("testtt", "getTodayPairs");
        }
        if (todayPairs != null) {
            todayPairs.removeChangeListeners();
        }
        RealmResults<Pair> results = realm.where(Pair.class)
                .equalTo("scheduleSubject.id", Prefs.get().getSelectedThingId())
                .equalTo("date", LocalDate.now().toString())
                .findAllSorted("number");
        results.addChangeListener(new RealmChangeListener<RealmResults<Pair>>() {
            @Override
            public void onChange(RealmResults<Pair> element) {
                updateNotifications(false);
            }
        });
        return results;
    }

    private void updateNotifications(boolean loadFromRealm) {
        if (BuildConfig.DEBUG) {
            Log.d("testtt", "updateNotifications");
        }
        if (loadFromRealm) todayPairs = getTodayPairs();
        removeNotifications();
        for (Pair p : todayPairs) {
            if (!p.isNotified() && p.getStartDateTime().isAfterNow()) {
                createNotification(p);
            }
        }
    }

    private void removeNotifications() {
        if (BuildConfig.DEBUG) {
            Log.d("testtt", "removeNotifications");
        }
        while (pendingIntentStack.size() > 0) {
            alarmManager.cancel(pendingIntentStack.pop());
        }
    }

    private void createNotification(Pair pair) {
        if (BuildConfig.DEBUG) {
            Log.d("testtt", "create notification for " + pair.getId());
        }
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.PAIR_ID, pair.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        DateTime dateTime = pair.getStartDateTime();
        if (BuildConfig.DEBUG) {
            Log.d("testtt", pair.getId() + " wait for "
                    + (dateTime.minusMinutes(10).getMillis() - DateTime.now().getMillis()) / 1000
                    + " seconds");
        }

        pendingIntentStack.push(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    dateTime.minusMinutes(10).getMillis(),
                    pendingIntent
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    dateTime.minusMinutes(10).getMillis(),
                    pendingIntent
            );
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    dateTime.minusMinutes(10).getMillis(),
                    pendingIntent
            );
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Prefs.get().unregister(listener);
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        unregisterReceiver(timeChangedReceiver);
        super.onDestroy();
    }


}
