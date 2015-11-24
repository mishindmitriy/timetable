package mishindmitriy.timetable.model.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import mishindmitriy.timetable.model.data.entity.Config;

/**
 * Created by mishindmitriy on 22.11.2015.
 */
public class ConfigDAO extends BaseDaoImpl<Config, Integer> {

    protected ConfigDAO(ConnectionSource connectionSource,
                        Class<Config> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}
