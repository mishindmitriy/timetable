package mishindmitriy.timetable.utils;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import mishindmitriy.timetable.model.data.PeriodType;
import mishindmitriy.timetable.model.data.PeriodTypeConverter;
import mishindmitriy.timetable.model.data.Thing;

/**
 * Created by mishindmitriy on 12.11.2015.
 */
public class PreferencesHelper {

    public static final String APP_PREFERENCES = "timetable";
    public static final String FAVORITES = "favorites";
    public static final String PERIOD = "period";
    public static final String CURRENT_THING = "current_thing";

    private SharedPreferences preferences;
    private Gson gson = new Gson();
    private static PreferencesHelper ourInstance;

    public static PreferencesHelper getInstance() {
        return ourInstance;
    }

    private PreferencesHelper(SharedPreferences preferences)
    {
        this.preferences=preferences;
    }

    public static void init(SharedPreferences preferences)
    {
        PreferencesHelper.ourInstance=new PreferencesHelper(preferences);
    }

    public void saveThing(Thing thing)
    {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(CURRENT_THING, gson.toJson(thing));
        editor.apply();
    }

    public Thing loadThing()
    {
        final String json = preferences.getString(CURRENT_THING, null);
        return gson.fromJson(json, Thing.class);
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
