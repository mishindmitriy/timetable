package mishindmitriy.timetable;
import android.app.Application;

import org.acra.*;
import org.acra.annotation.*;
import com.firebase.client.Firebase;
/**
 * Created by mishindmitriy on 25.08.2015.
 */
@ReportsCrashes(
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class TimeTableApp extends Application {
    @Override
    public void onCreate() {
        Firebase.setAndroidContext(this);
        FirebaseReportSender sender=new FirebaseReportSender();

        ACRA.init(this);
        ACRA.getErrorReporter().setReportSender(sender);
        ACRA.getErrorReporter().checkReportsOnApplicationStart();
        super.onCreate();
    }


}