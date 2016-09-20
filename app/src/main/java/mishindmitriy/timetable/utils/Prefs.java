package mishindmitriy.timetable.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dmitriy on 19.09.16.
 */
public class Prefs {
    public final static String KEY_SELECTED_THING_SERVER_ID = "selected_thing_server_id";
    private static Prefs instance;
    final private SharedPreferences prefs;

    public Prefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public static Prefs get() {
        return instance;
    }

    public static void init(Context context) {
        instance = new Prefs(context.getSharedPreferences("tolgas.prefs", Context.MODE_PRIVATE));
    }

    public String getSelectedThingServerId() {
        return prefs.getString(KEY_SELECTED_THING_SERVER_ID, null);
    }

    public void setSelectedThingServerId(String thingServerId) {
        if (thingServerId == null || thingServerId.isEmpty()) return;
        prefs.edit().putString(KEY_SELECTED_THING_SERVER_ID, thingServerId)
                .apply();
    }

    public void register(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregister(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
