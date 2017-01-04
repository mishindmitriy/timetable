package mishindmitriy.timetable.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.schedulesubjects.ScheduleSubjectsActivity;
import mishindmitriy.timetable.app.shedule.ScheduleActivity;
import mishindmitriy.timetable.utils.Prefs;

/**
 * Created by mishindmitriy on 02.11.2016.
 */

public class SplashScreen extends AppCompatActivity {
    @Inject
    protected Prefs prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TimeTableApp.component().inject(this);
        try {
            if (prefs.getSelectedThingId() > 0) {
                startActivity(new Intent(this, ScheduleActivity.class));
            } else {
                startActivity(new Intent(this, ScheduleSubjectsActivity.class));
            }
        } finally {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}
