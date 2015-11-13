package mishindmitriy.timetable.model;

import android.database.Observable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private HashMap<String,Thing> mThingCasesMap=new HashMap<>();//TODO

    public CaseThingModel(ThingType thing) {
        Log.i(TAG, "new Instance");
        this.mWhatCase = thing;
    }

    public List<String> getNameList() {
        Iterator<Thing> i = this.mListThingCases.iterator();
        List<String> groupNameList = new ArrayList<>();
        while (i.hasNext()) {
            Thing g = i.next();
            groupNameList.add(g.getThingName());
        }
        return groupNameList;
    }

    public int getPositionByName(CharSequence groupName) {
        int pos = 0;
        Iterator iterator = this.mListThingCases.iterator();
        while (!this.mListThingCases.get(pos).getThingName().equals(groupName)) {
            iterator.next();
            pos++;
        }
        return pos;
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
        if (this.mIsWorking) observer.onLoadStarted(this);
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
        void onLoadStarted(CaseThingModel caseGroupModel);

        void onLoadFinished(CaseThingModel caseGroupModel);

        void onLoadFailed(CaseThingModel caseGroupModel);
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
                observer.onLoadStarted(CaseThingModel.this);
            }
        }

        public void notifySucceeded() {
            for (final Observer observer : this.mObservers) {
                observer.onLoadFinished(CaseThingModel.this);
            }
        }

        public void notifyFailed() {
            for (final Observer observer : this.mObservers) {
                observer.onLoadFailed(CaseThingModel.this);
            }
        }
    }

}
