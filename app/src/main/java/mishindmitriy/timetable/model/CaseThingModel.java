package mishindmitriy.timetable.model;

import android.database.Observable;
import android.os.AsyncTask;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mishindmitriy.timetable.model.data.ThingType;
import mishindmitriy.timetable.model.data.entity.Thing;
import mishindmitriy.timetable.model.db.DatabaseHelper;
import mishindmitriy.timetable.model.db.HelperFactory;
import mishindmitriy.timetable.model.db.ThingDAO;
import mishindmitriy.timetable.utils.ParseHelper;

/**
 * Created by mishindmitriy on 04.09.2015.
 */
public class CaseThingModel {
    private static final String TAG = "CaseModel";
    private final LoadDataObservable mObservable = new LoadDataObservable();
    private final ThingType mThingType;
    public LoadDataTask mLoadTask;
    private boolean mIsWorking;
    private List<Thing> mListThings = new ArrayList<>();

    public CaseThingModel(ThingType thing) {
        this.mThingType = thing;
    }

    public boolean isWorking() {
        return this.mIsWorking;
    }

    public List<Thing> getList() {
        return this.mListThings;
    }

    public void loadData() {
        if (this.mIsWorking) return;
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

    private void saveCache() {
        try {
            HelperFactory.getInstance().getThingGAO().saveListThings(mListThings);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCache() {
        List<Thing> things=null;
        try {
            ThingDAO dao= HelperFactory.getInstance().getThingGAO();
            things=dao.loadListThings(mThingType);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (things!=null) mListThings=things;
    }

    public interface Observer {
        void onLoadStarted();

        void onLoadFinished(List<Thing> listThings);

        void onLoadFailed();
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(final Void... params) {
            try {
                mListThings =ParseHelper.getSomeThing(mThingType);
            } catch (IOException e) {
                e.printStackTrace();
            }
            CaseThingModel.this.saveCache();
            CaseThingModel.this.loadCache();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            CaseThingModel.this.mIsWorking = false;

            if (success) {
                CaseThingModel.this.mObservable.notifySucceeded();
            } else {
                CaseThingModel.this.mObservable.notifyFailed();
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
                observer.onLoadFinished(CaseThingModel.this.mListThings);
            }
        }

        public void notifyFailed() {
            for (final Observer observer : this.mObservers) {
                observer.onLoadFailed();
            }
        }
    }

}
