package mishindmitriy.timetable;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;

import java.util.Calendar;

/**
 * Created by mishindmitriy on 26.08.2015.
 */
public class FirebaseReportSender  implements ReportSender {

    public FirebaseReportSender(){
        // initialize your sender with needed parameters
        Log.d("FirebaseReportSender","sender Created");
    }

    @Override
    public void send(Context context, CrashReportData errorContent)  {
        Firebase myFirebaseRef = new Firebase("https://torrid-fire-6647.firebaseio.com/reports/new/"+
                errorContent.getProperty(ReportField.PHONE_MODEL)+" "+
                errorContent.getProperty(ReportField.INSTALLATION_ID)+"/"+
                Calendar.getInstance().getTime().toString());


        myFirebaseRef.child("ANDROID_VERSION").setValue(errorContent.getProperty(ReportField.ANDROID_VERSION));
        myFirebaseRef.child("APP_VERSION_NAME").setValue(errorContent.getProperty(ReportField.APP_VERSION_NAME));
        myFirebaseRef.child("CUSTOM_DATA").setValue(errorContent.getProperty(ReportField.CUSTOM_DATA));
        myFirebaseRef.child("STACK_TRACE").setValue(errorContent.getProperty(ReportField.STACK_TRACE));
        myFirebaseRef.child("APP_VERSION_NAME").setValue(errorContent.getProperty(ReportField.APP_VERSION_NAME));
        myFirebaseRef.child("APP_VERSION_NAME").setValue(errorContent.getProperty(ReportField.APP_VERSION_NAME));
    }
}