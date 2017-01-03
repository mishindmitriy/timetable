package mishindmitriy.timetable.app.notifications;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.app.TimeTableApp;
import mishindmitriy.timetable.model.Pair;
import mishindmitriy.timetable.utils.Prefs;

/**
 * Created by mishindmitriy on 27.09.2016.
 */

public class NotificationService extends IntentService {
    private static final String ACTION_SHOW_NOTIFICATION = "com.mishindmitriy.timetable.ACTION.show_notification";

    @Inject
    protected Prefs prefs;

    public NotificationService() {
        super(NotificationService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TimeTableApp.component().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        updateRepeatedIntent();
        updateNotifications();
    }

    private void updateRepeatedIntent() {
        Log.d("testtt", "updateRepeatedIntent");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationService.class);
        alarmManager.setInexactRepeating(
                AlarmManager.RTC,
                DateTime.now().plusDays(1).withTimeAtStartOfDay().getMillis(),
                AlarmManager.INTERVAL_DAY,
                PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        );
    }

    private RealmResults<Pair> getTodayPairs(Realm realm) {
        return realm.where(Pair.class)
                .equalTo("scheduleSubject.id", prefs.getSelectedThingId())
                .equalTo("date", LocalDate.now().toString())
                .findAllSorted("number");
    }

    private void updateNotifications() {
        if (BuildConfig.DEBUG) {
            Log.d("testtt", "updateNotifications");
        }
        removeNotifications();
        Set<String> ids = new HashSet<>();

        Realm realm = Realm.getDefaultInstance();
        try {
            for (Pair p : getTodayPairs(realm)) {
                if (!p.isNotified() && p.getStartDateTime().isAfterNow()) {
                    createNotification(p);
                    ids.add(String.valueOf(p.getId()));
                }
            }
        } finally {
            realm.close();
        }

        prefs.setPendingIntentPairsIds(ids);
    }

    private void removeNotifications() {
        for (long id : prefs.getPendingIntentPairsIds()) {
            createPendingIntentToPublisherReceiver(id, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
        }
    }

    private PendingIntent createPendingIntentToPublisherReceiver(long id, int flag) {
        Intent notificationIntent = new Intent();
        notificationIntent.setAction(ACTION_SHOW_NOTIFICATION);
        notificationIntent.putExtra(NotificationPublisherReceiver.PAIR_ID, id);

        return PendingIntent.getBroadcast(this, 0, notificationIntent, flag);
    }

    private void createNotification(Pair pair) {
        if (BuildConfig.DEBUG) {
            Log.d("testtt", "create notification for " + pair.getId());
        }

        PendingIntent pendingIntent = createPendingIntentToPublisherReceiver(
                pair.getId(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        DateTime dateTime = pair.getStartDateTime();
        if (BuildConfig.DEBUG) {
            Log.d("testtt",
                    pair.getId() + " wait for "
                            + (dateTime.minusMinutes(10).getMillis() - DateTime.now().getMillis()) / 1000
                            + " seconds"
            );
        }

        if (BuildConfig.DEBUG) {
            Log.d("testtt", "notify on " + dateTime.minusMinutes(10).toString());
        }
        setPendingIntent(pendingIntent, dateTime.minusMinutes(10).getMillis());
    }

    private void setPendingIntent(PendingIntent pendingIntent, long millis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
        }
    }


    @Override
    public void onDestroy() {
        Log.d("testtt", "service onDestroy");
        super.onDestroy();
    }
}
