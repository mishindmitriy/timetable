package mishindmitriy.timetable.utils;

import android.content.ContentResolver;
import android.provider.Settings;

import com.firebase.client.Firebase;

import org.joda.time.DateTime;

/**
 * Created by mishindmitriy on 24.02.2017.
 */

public class FirebaseHelper {
    private static Firebase createFirebase() {
        return new Firebase("https://timetable-cbbbf.firebaseio.com/");
    }

    public static void sendFeedback(ContentResolver contentResolver, String feedback) {
        final String androidId = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
        );
        createFirebase().child("feedback")
                .child(androidId)
                .child(String.valueOf(DateTime.now().getMillis()))
                .setValue(feedback);
    }
}
