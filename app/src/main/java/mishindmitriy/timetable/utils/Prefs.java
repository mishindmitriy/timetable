package mishindmitriy.timetable.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dmitriy on 19.09.16.
 */
public class Prefs {
    public final static String KEY_SELECTED_THING_ID = "selected_thing_id";
    public final static String KEY_NOTIFICATIONS = "notifications";
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

    public long getSelectedThingId() {
        return prefs.getLong(KEY_SELECTED_THING_ID, 0);
    }

    public void setSelectedThingId(long thingId) {
        if (thingId == 0) return;
        prefs.edit().putLong(KEY_SELECTED_THING_ID, thingId)
                .apply();
    }

    public void register(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregister(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS, false);
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled)
                .apply();
    }
}
