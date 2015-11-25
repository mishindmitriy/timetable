package mishindmitriy.timetable.model.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by mishindmitriy on 25.11.2015.
 */
public class HelperFactory {
    private static DatabaseHelper instance;
    public static void init(Context context) {
        instance = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public synchronized static DatabaseHelper getInstance() {
        return instance;
    }

    public synchronized static void release()
    {
        instance.close();
        OpenHelperManager.releaseHelper();
        instance=null;
    }
}
