package mishindmitriy.timetable.model;

import android.database.Observable;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mishindmitriy.timetable.model.data.Thing;
import mishindmitriy.timetable.model.data.ThingType;
import mishindmitriy.timetable.utils.ParseHelper;

/**
 * Created by mishindmitriy on 04.09.2015.
 */
public class CaseThingModel {
    private static final String TAG = "CaseModel";
    private final LoadDataObservable mObservable = new LoadDataObservable();
    private final ThingType mWhatCase;
    public LoadDataTask mLoadTask;
    private boolean mIsWorking;
    private List<Thing> mListThingCases = new ArrayList<>();

    public CaseThingModel(ThingType thing) {
        this.mWhatCase = thing;
    }

    public boolean isWorking() {
        return this.mIsWorking;
    }

    public List<Thing> getList() {
        return this.mListThingCases;
    }

    public void LoadData() {
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

    public void StopLoad() {
        if (this.mIsWorking) {
            this.mLoadTask.cancel(true);
            this.mIsWorking = false;
        }
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
                switch (mWhatCase) {
                    case GROUP:
                        CaseThingModel.this.mListThingCases = ParseHelper.getGroupsList();
                        break;
                    case TEACHER:
                        CaseThingModel.this.mListThingCases = ParseHelper.getTeachersList();
                        break;
                    case CLASSROOM:
                        CaseThingModel.this.mListThingCases = ParseHelper.getClassroomsList();
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
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
                observer.onLoadFinished(CaseThingModel.this.mListThingCases);
            }
        }

        public void notifyFailed() {
            for (final Observer observer : this.mObservers) {
                observer.onLoadFailed();
            }
        }
    }

}
