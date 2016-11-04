package mishindmitriy.timetable.app.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.joda.time.DateTime;

import io.realm.Realm;
import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.shedule.ScheduleActivity_;
import mishindmitriy.timetable.model.Pair;

/**
 * Created by mishindmitriy on 30.10.2016.
 */

public class NotificationPublisherReceiver extends BroadcastReceiver {
    public static String PAIR_ID = "notification-id";

    public void onReceive(Context context, Intent intent) {
        if (BuildConfig.DEBUG) {
            Log.d("testtt", "notify " + intent.getLongExtra(PAIR_ID, 0));
        }
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Realm realm = Realm.getDefaultInstance();
        final long id = intent.getLongExtra(PAIR_ID, 0);
        Pair pair = realm.where(Pair.class)
                .equalTo("id", id)
                .findFirst();
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, ScheduleActivity_.class), PendingIntent.FLAG_UPDATE_CURRENT);
        if (pair.getStartDateTime().isBeforeNow()) {
            long msLeft = pair.getStartDateTime().getMillis() - DateTime.now().getMillis();
            int minutesLeft = (int) Math.abs(msLeft / 1000 * 60);
            notificationManager.notify(Long.valueOf(id).intValue(),
                    new NotificationCompat.Builder(context)
                            .setContentTitle(pair.getSubject())
                            .setContentText("Через " + minutesLeft + " минут, аудитория " + pair.getClassroom())
                            .setContentIntent(contentIntent)
                            .setSmallIcon(R.drawable.ic_room_white_18dp)
                            .setLargeIcon(
                                    BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)
                            )
                            .setAutoCancel(true)
                            .build());
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Pair.class).equalTo("id", id)
                        .findFirst().setNotified();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                realm.close();
            }
        });
    }
}
