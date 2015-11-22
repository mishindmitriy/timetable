package mishindmitriy.timetable.utils;

import android.content.SharedPreferences;

import mishindmitriy.timetable.model.data.PeriodType;
import mishindmitriy.timetable.model.data.PeriodTypeConverter;

/**
 * Created by mishindmitriy on 12.11.2015.
 */
public class PreferencesHelper {

    public static final String APP_PREFERENCES = "timetable";
    public static final String PERIOD = "period";
    private static PreferencesHelper ourInstance;
    private SharedPreferences preferences;

    private PreferencesHelper(SharedPreferences preferences)
    {
        this.preferences=preferences;
    }

    public static PreferencesHelper getInstance() {
        return ourInstance;
    }

    public static void init(SharedPreferences preferences)
    {
        PreferencesHelper.ourInstance=new PreferencesHelper(preferences);
    }

    public void saveOutputPeriod(PeriodType period)
    {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(PERIOD, period.toString());
        editor.apply();
    }

    public PeriodType loadOutputPeriod() {
        return PeriodTypeConverter.getPeriodTypeFromString(preferences.getString(PERIOD, PeriodType.TODAY.toString()));
    }
}
