package mishindmitriy.timetable.app.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mishindmitriy on 04.11.2016.
 */

public class UpdateNotificationsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("testtt", "wakeup service onReceive");
        context.startService(new Intent(context, NotificationService.class));
    }
}
