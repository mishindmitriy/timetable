package mishindmitriy.timetable.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.schedulesubjects.ScheduleSubjectsActivity_;
import mishindmitriy.timetable.app.shedule.SheduleActivity_;
import mishindmitriy.timetable.utils.Prefs;

/**
 * Created by mishindmitriy on 02.11.2016.
 */

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (Prefs.get().getSelectedThingId() > 0) {
                SheduleActivity_.intent(this).start();
            } else {
                ScheduleSubjectsActivity_.intent(this).start();
            }
        } finally {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}
