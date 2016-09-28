package mishindmitriy.timetable.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mishindmitriy on 27.09.2016.
 */

public class NotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getByteExtra(NOTIFICATION_ID, (byte) 0);
        notificationManager.notify(id, notification);
        Log.d("testtt",
                System.currentTimeMillis() + "ms "
                        + "notified pair " + id
        );
    }
}
