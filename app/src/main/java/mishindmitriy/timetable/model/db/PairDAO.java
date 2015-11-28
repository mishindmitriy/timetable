package mishindmitriy.timetable.model.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import mishindmitriy.timetable.model.data.entity.Pair;
import mishindmitriy.timetable.model.data.entity.Thing;

/**
 * Created by mishindmitriy on 21.11.2015.
 */
public class PairDAO extends BaseDaoImpl<Pair, Integer> {

    protected PairDAO(ConnectionSource connectionSource,
                      Class<Pair> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public void saveListPairs(final List<Pair> pairs) throws SQLException {
        TransactionManager transactionManager = new TransactionManager(getConnectionSource());
        Callable transaction = new Callable() {
            @Override
            public Void call() throws Exception {
                for (Pair pair : pairs) {
                    QueryBuilder<Pair, Integer> queryBuilder = queryBuilder();
                    queryBuilder.where()
                            .eq("date", pair.getDate())
                            .and()
                            .eq("classroom", pair.getClassroom())
                            .and()
                            .eq("number", pair.getNumber())
                            .and()
                            .eq("type", pair.getType())
                            .and()
                            .eq("subject", pair.getSubject())
                            .and()
                            .eq("teacher", pair.getTeacher())
                            //.and()
                            //.eq("note", pair.getNote())
                            ;
                    if (queryForFirst(queryBuilder.prepare()) == null) {
                        PairDAO.this.create(pair);
                    }
                }
                return null;
            }
        };
        transactionManager.callInTransaction(transaction);
    }

    public List<Pair> loadListPairs(Thing thing, Date from, Date to) throws SQLException {
        QueryBuilder<Pair, Integer> queryBuilder = queryBuilder();
        queryBuilder.where()
                .eq("thing_id", thing.getId())
                .and()
                .ge("date", from)
                .and()
                .lt("date", to);
        return query(queryBuilder.prepare());
    }
}
