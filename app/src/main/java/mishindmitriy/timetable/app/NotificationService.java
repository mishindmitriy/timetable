package mishindmitriy.timetable.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Stack;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.shedule.SheduleActivity_;
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
                updateNotifications();
            }
        }
    };
    private SharedPreferences.OnSharedPreferenceChangeListener listener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Prefs.KEY_SELECTED_THING_ID)) {
                updateNotifications();
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
        updateNotifications();
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
        updateNotifications();
    }

    private RealmResults<Pair> getTodayPairs() {
        if (todayPairs != null) {
            todayPairs.removeChangeListeners();
        }
        RealmResults<Pair> results = realm.where(Pair.class)
                .equalTo("thing.id", Prefs.get().getSelectedThingId())
                .equalTo("date", LocalDate.now().toString())
                .findAllSorted("number");
        results.addChangeListener(new RealmChangeListener<RealmResults<Pair>>() {
            @Override
            public void onChange(RealmResults<Pair> element) {
                updateNotifications();
            }
        });
        return results;
    }

    private void updateNotifications() {
        todayPairs = getTodayPairs();
        removeNotifications();
        for (Pair p : todayPairs) {
            if (p.getStartDateTime().isAfterNow()) {
                createNotification(p);
            }
        }
    }

    private void removeNotifications() {
        while (pendingIntentStack.size() > 0) {
            alarmManager.cancel(pendingIntentStack.pop());
        }
    }

    private void createNotification(Pair p) {
        Intent intent = new Intent(this, SheduleActivity_.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, (int) p.getId());
        notificationIntent.putExtra(
                NotificationPublisher.NOTIFICATION,
                new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle(p.getSubject())
                        .setContentText("Через 10 минут, аудитория " + p.getClassroom())
                        .setContentIntent(contentIntent)
                        .setLargeIcon(
                                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
                        )
                        .setAutoCancel(true)
                        .build()
        );

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        DateTime dateTime = p.getStartDateTime();

        pendingIntentStack.add(pendingIntent);
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                dateTime.minusMinutes(10).getMillis(),
                pendingIntent
        );
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

    public static class NotificationPublisher extends BroadcastReceiver {
        public static String NOTIFICATION_ID = "notification-id";
        public static String NOTIFICATION = "notification";

        public void onReceive(Context context, Intent intent) {
            NotificationManager notificationManager
                    = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = intent.getParcelableExtra(NOTIFICATION);
            int id = intent.getIntExtra(NOTIFICATION_ID, 0);
            notificationManager.notify(id, notification);
        }
    }
}
