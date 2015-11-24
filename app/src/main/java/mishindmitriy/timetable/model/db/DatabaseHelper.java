package mishindmitriy.timetable.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import mishindmitriy.timetable.model.data.entity.Config;
import mishindmitriy.timetable.model.data.entity.Pair;
import mishindmitriy.timetable.model.data.entity.Thing;

/**
 * Created by mishindmitriy on 21.11.2015.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    //имя файла базы данных который будет храниться в /data/data/APPNAME/DATABASE_NAME.db
    private static final String DATABASE_NAME = "tolgas.db";
    //с каждым увеличением версии, при нахождении в устройстве БД с предыдущей версией будет выполнен метод onUpgrade();
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper instance;
    //ссылки на DAO соответсвующие сущностям, хранимым в БД
    private PairDAO pairDao = null;
    private ThingDAO thingGao = null;
    private ConfigDAO configDAO = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void init(Context context) {
        instance = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public static DatabaseHelper getInstance() {
        return instance;
    }

    //Выполняется, когда файл с БД не найден на устройстве
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Pair.class);
            TableUtils.createTable(connectionSource, Thing.class);
            TableUtils.createTable(connectionSource, Config.class);
        } catch (SQLException e) {
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    //Выполняется, когда БД имеет версию отличную от текущей
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer) {
        try {
            //Так делают ленивые, гораздо предпочтительнее не удаляя БД аккуратно вносить изменения
            TableUtils.dropTable(connectionSource, Pair.class, true);
            TableUtils.dropTable(connectionSource, Thing.class, true);
            TableUtils.dropTable(connectionSource, Config.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "error upgrading db " + DATABASE_NAME + "from ver " + oldVer);
            throw new RuntimeException(e);
        }
    }

    //синглтон для GoalDAO
    public PairDAO getPairDAO() throws SQLException {
        if (pairDao == null) {
            pairDao = new PairDAO(getConnectionSource(), Pair.class);
        }
        return pairDao;
    }

    public ThingDAO getThingGAO() throws SQLException {
        if (thingGao == null) {
            thingGao = new ThingDAO(getConnectionSource(), Thing.class);
        }
        return thingGao;
    }

    public ConfigDAO getConfigDAO() throws SQLException {
        if (configDAO == null) {
            configDAO = new ConfigDAO(getConnectionSource(), Config.class);
        }
        return configDAO;
    }

    //выполняется при закрытии приложения
    @Override
    public void close() {
        super.close();
        pairDao = null;
        thingGao = null;
        configDAO = null;
    }
}
