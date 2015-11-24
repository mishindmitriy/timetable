package mishindmitriy.timetable.model.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import mishindmitriy.timetable.model.data.ThingType;
import mishindmitriy.timetable.model.data.entity.Thing;

/**
 * Created by mishindmitriy on 22.11.2015.
 */
public class ThingDAO extends BaseDaoImpl<Thing, Integer> {

    protected ThingDAO(ConnectionSource connectionSource,
                       Class<Thing> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public void saveListThings(final List<Thing> things) throws SQLException {
        if (things == null) return;
        TransactionManager transactionManager = new TransactionManager(getConnectionSource());
        Callable transaction = new Callable() {
            @Override
            public Void call() throws Exception {
                for (Thing thing : things) {
                    QueryBuilder<Thing, Integer> queryBuilder = queryBuilder();
                    queryBuilder.where()
                            .eq("serverId", thing.getServerId())
                            .and()
                            .eq("type", thing.getType());
                    if (queryForFirst(queryBuilder.prepare()) == null) {
                        ThingDAO.this.create(thing);
                    }
                }
                return null;
            }
        };
        transactionManager.callInTransaction(transaction);
    }

    public List<Thing> loadListThings(ThingType mThingType) {
        List<Thing> list = null;
        QueryBuilder<Thing, Integer> queryBuilder = queryBuilder();
        try {
            queryBuilder.where().eq("type", mThingType);
            list = query(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Thing> loadFavorites() {
        List<Thing> list = null;
        QueryBuilder<Thing, Integer> queryBuilder = queryBuilder();
        try {
            queryBuilder.where().eq("favorite", true);
            list = query(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
