package mishindmitriy.timetable.model;

import android.database.Observable;
import android.os.AsyncTask;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mishindmitriy.timetable.model.data.PeriodType;
import mishindmitriy.timetable.model.data.PeriodTypeConverter;
import mishindmitriy.timetable.model.data.entity.Config;
import mishindmitriy.timetable.model.data.entity.Pair;
import mishindmitriy.timetable.model.data.entity.Thing;
import mishindmitriy.timetable.model.db.ConfigDAO;
import mishindmitriy.timetable.model.db.DatabaseHelper;
import mishindmitriy.timetable.model.db.HelperFactory;
import mishindmitriy.timetable.model.db.PairDAO;
import mishindmitriy.timetable.utils.ParseHelper;

/**
 * Model for Shedule. Include all operation with shedule
 * Created by mishindmitriy on 04.09.2015.
 */
public class SheduleModel {
    private static final String TAG = "SheduleModel";
    private final LoadDataObservable mObservable = new LoadDataObservable();
    private Config config=null;
    private boolean mIsWorking=false;
    private LoadDataTask mLoadTask=null;
    private List<Pair> mShedule= new ArrayList<>();

    public SheduleModel() {
        loadConfig();
        if (config != null && config.getCurrentThing() == null) {
            setCurrentFirstOfFavorites();
        }
    }

    private void setCurrentFirstOfFavorites() {
        List<Thing> favorites = getFavoritesThings();
        if (favorites != null && favorites.size() > 0) setThing(favorites.get(0));
    }

    public void setThing(Thing thing)
    {
        //if (thing==null) return;
        config.setCurrentThing(thing);
        updateConfig();
        loadData();
        mObservable.notifyCurrentThingChange();
    }

    private void loadConfig()
    {
        try {
            config=HelperFactory.getInstance().getConfigDAO().queryForId(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (config==null)
        {
            config=new Config();
            try {
                ConfigDAO dao=HelperFactory.getInstance().getConfigDAO();
                dao.create(config);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateConfig() {
        try {
            ConfigDAO dao=HelperFactory.getInstance().getConfigDAO();
            dao.update(config);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPeriod(final PeriodType period) {
        if (config.getPeriodType()==period) return;
        config.setPeriodType(period);
        updateConfig();
    }

    public void setPeriod(int position)
    {
        setPeriod(PeriodTypeConverter.getPeriodTypeByPosition(position));
    }

    public int getPeriodPosition() {
        return PeriodTypeConverter.getPositionByPeriod(config.getPeriodType());
    }

    public boolean isWorking() {
        return this.mIsWorking;
    }

    public CharSequence getThingName() {
        if (config.getCurrentThing() == null) return null;
        return config.getCurrentThing().getName();
    }

    public boolean isThingAvailable() {
        return config!=null && config.getCurrentThing()!=null;
    }

    public List<Pair> getShedule() {
        return this.mShedule;
    }

    public void loadData() {
        if (this.mIsWorking) stopLoad();

        if (!isThingAvailable()
                ||config==null
                ||config.getCurrentThing()==null
                ||config.getCurrentThing().getServerId()==null
                || config.getPeriodType() == null) return;

        loadCache();
        mObservable.notifyCacheLoad();

        this.mObservable.notifyStarted();

        this.mIsWorking = true;
        this.mLoadTask = new LoadDataTask();
        this.mLoadTask.execute();
    }

    public void registerObserver(final Observer observer) {
        this.mObservable.registerObserver(observer);
        if (this.mIsWorking) observer.onLoadStarted();
    }

    public void unregisterObserver(final Observer observer) {
        this.mObservable.unregisterObserver(observer);
    }

    public void stopLoad() {
        if (this.mIsWorking) {
            this.mLoadTask.cancel(true);
            this.mIsWorking = false;
        }
    }

    private Date getToDate() {
        final Calendar cal = getFromCalendar();
        int offset = 0;
        switch (config.getPeriodType()) {
            case SEVEN_DAYS:
            case THIS_WEEK:
            case NEXT_WEEK:
                offset = 6;
                break;
            case THIS_MONTH:
            case NEXT_MONTH:
                offset = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - 1;
                break;
        }
        cal.roll(Calendar.DAY_OF_YEAR, offset);
        if (cal.get(Calendar.DAY_OF_YEAR)-offset<0) cal.roll(Calendar.YEAR,1);
        cal.set(Calendar.HOUR_OF_DAY,23);
        return cal.getTime();
    }

    private Calendar getFromCalendar()
    {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND,0);
        switch (config.getPeriodType()) {
            case TODAY:
                break;
            case TOMORROW:
                cal.roll(Calendar.DAY_OF_YEAR, 1);
                break;
            case SEVEN_DAYS:
                break;
            case THIS_WEEK:
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    cal.roll(Calendar.DAY_OF_YEAR, -1);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case NEXT_WEEK:
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    cal.roll(Calendar.DAY_OF_YEAR, 5);
                else cal.roll(Calendar.DAY_OF_YEAR, 7);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case THIS_MONTH:
                final int actualMinimum = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DAY_OF_MONTH, actualMinimum);
                break;
            case NEXT_MONTH:
                cal.roll(Calendar.DAY_OF_YEAR, 30);
                final int actualMinimum1 = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DAY_OF_MONTH, actualMinimum1);
                break;
        }
        return cal;
    }

    private Date getFromDate() {
        return getFromCalendar().getTime();
    }

    private void saveCache() {
        DatabaseHelper helper= HelperFactory.getInstance();
        try {
            helper.getPairDAO().saveListPairs(mShedule);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCache() {
        List<Pair> pairs=null;
        if (config == null || config.getCurrentThing() == null) return;
        try {
            PairDAO dao= HelperFactory.getInstance().getPairDAO();
            pairs=dao.loadListPairs(config.getCurrentThing(), getFromDate(), getToDate());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (pairs!=null) mShedule=pairs;
    }

    public List<Thing> getFavoritesThings() {
        List<Thing> list=null;
        try {
            list=HelperFactory.getInstance().getThingGAO().loadFavorites();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Thing getCurrentThing() {
        return config.getCurrentThing();
    }

    public void checkCurrentThing() {
        try {
            HelperFactory.getInstance().getThingGAO().refresh(config.getCurrentThing());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (config.getCurrentThing() != null && !config.getCurrentThing().isFavorite())
            setThing(null);
        if (config.getCurrentThing() == null) setCurrentFirstOfFavorites();
    }

    public interface Observer {
        void onLoadStarted();

        void onLoadFinished(List<Pair> shedule);

        void onCacheLoad(List<Pair> shedule);

        void onLoadFail();

        void onCurrentThingChange(Thing thing);
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(final Void... arg) {
            try {
                if (config.getCurrentThing()==null) return false;
                SheduleModel.this.mShedule = ParseHelper.getShedule(config.getCurrentThing(), SheduleModel.this.getFromDate(),SheduleModel.this.getToDate());
            } catch (final IOException e) {
                SheduleModel.this.mShedule = new ArrayList<>();
                return false;
            }
            if (SheduleModel.this.mShedule == null) {
                SheduleModel.this.mShedule = new ArrayList<>();
            } else SheduleModel.this.saveCache();
            SheduleModel.this.loadCache();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            SheduleModel.this.mIsWorking = false;
            if (success) {
                SheduleModel.this.mObservable.notifySucceeded();
            } else {
                SheduleModel.this.mObservable.notifyFailed();
            }
        }
    }

    private class LoadDataObservable extends Observable<Observer> {

        public void notifyStarted() {
            for (final Observer observer : this.mObservers) {
                observer.onLoadStarted();
            }
        }

        public void notifySucceeded() {
            for (final Observer observer : this.mObservers) {
                observer.onLoadFinished(SheduleModel.this.getShedule());
            }
        }

        public void notifyFailed() {
            for (final Observer observer : this.mObservers) {
                observer.onLoadFail();
            }
        }

        public void notifyCacheLoad() {
            for (final Observer observer : this.mObservers) {
                observer.onCacheLoad(SheduleModel.this.getShedule());
            }
        }

        public void notifyCurrentThingChange() {
            for (final Observer observer : this.mObservers) {
                observer.onCurrentThingChange(SheduleModel.this.config.getCurrentThing());
            }
        }
    }

}
