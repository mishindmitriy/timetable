package mishindmitriy.timetable.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Provides;
import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.app.ApplicationContext;

/**
 * Created by dmitriy on 19.09.16.
 */
public class Prefs {
    public final static String KEY_SELECTED_THING_ID = "selected_thing_id";
    public final static String KEY_NOTIFICATIONS = "notifications";
    public final static String KEY_SCHEDULE_SUBJECTS_LAST_UPDATE = "subjects_last_update";
    private static final String KEY_PENDING_IDS = "pending_intents_ids";
    private static final String KEY_SCHEDULE_LAST_UPDATE = "schedule_last_update";
    private static final String KEY_UPDATE_DATE = "update_date";
    private static final String KEY_FEEDBACK_SHOWED = "feedback_showed";
    final private SharedPreferences prefs;

    public Prefs(SharedPreferences prefs) {
        this.prefs = prefs;
        if (BuildConfig.VERSION_CODE > 16 && !prefs.contains(KEY_UPDATE_DATE)) {
            prefs.edit()
                    .putLong(KEY_UPDATE_DATE, DateTime.now().getMillis())
                    .apply();
        }
    }

    public long getSelectedThingId() {
        return prefs.getLong(KEY_SELECTED_THING_ID, 0);
    }

    public void setSelectedThingId(long thingId) {
        if (thingId == 0) return;
        prefs.edit()
                .putLong(KEY_SELECTED_THING_ID, thingId)
                .putLong(KEY_SCHEDULE_LAST_UPDATE, 0)
                .apply();
    }

    public long getSubjectsLastUpdate() {
        return prefs.getLong(KEY_SCHEDULE_SUBJECTS_LAST_UPDATE, 0);
    }

    public void setSubjectsLastUpdate() {
        prefs.edit()
                .putLong(KEY_SCHEDULE_SUBJECTS_LAST_UPDATE, DateTime.now().getMillis())
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

    public HashSet<Long> getPendingIntentPairsIds() {
        Set<String> ids = prefs.getStringSet(KEY_PENDING_IDS, new HashSet<String>());
        HashSet<Long> longIds = new HashSet<>();
        for (String s : ids) {
            longIds.add(Long.valueOf(s));
        }
        return longIds;
    }

    public void setPendingIntentPairsIds(Set<String> ids) {
        prefs.edit()
                .putStringSet(KEY_PENDING_IDS, ids)
                .apply();
    }

    public long getScheduleLastUpdate() {
        return prefs.getLong(KEY_SCHEDULE_LAST_UPDATE, 0);
    }

    public void setScheduleLastUpdate() {
        prefs.edit()
                .putLong(KEY_SCHEDULE_LAST_UPDATE, DateTime.now().getMillis())
                .apply();
    }

    public DateTime getUpdateDate() {
        return new DateTime(prefs.getLong(KEY_UPDATE_DATE, 0));
    }

    public boolean isFeedbackShowed() {
        return prefs.getBoolean(KEY_FEEDBACK_SHOWED, false);
    }

    public void setFeedbackShowed() {
        prefs.edit().putBoolean(KEY_FEEDBACK_SHOWED, true).apply();
    }

    @dagger.Module
    public static class SettingsModule {
        @Provides
        @Singleton
        public Prefs provideSettings(@ApplicationContext Context context) {
            return new Prefs(context.getSharedPreferences("tolgas.prefs", Context.MODE_PRIVATE));
        }
    }
}
