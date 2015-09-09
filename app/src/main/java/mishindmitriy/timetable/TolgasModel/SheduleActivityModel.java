package mishindmitriy.timetable.TolgasModel;

import android.content.SharedPreferences;
import android.database.Observable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import mishindmitriy.timetable.PreferensesConst;

/**
 * Created by mishindmitriy on 04.09.2015.
 */
public class SheduleActivityModel {
    private static final String TAG = "SheduleModel";
    private boolean mIsWorking;
    private LoadDataTask mLoadTask;
    private final LoadDataObservable mObservable = new LoadDataObservable();

    public void setPeriod(byte mPeriod) {
        Log.i(TAG, "setPeriod=" + mPeriod);
        this.mPeriod = mPeriod;
    }

    private byte mPeriod;
    private String mGroupId;
    private CharSequence mGroupName;
    private List<DayPairs> mShedule;
    private String mCacheDir = null;

    public byte getPeriod() {
        return mPeriod;
    }

    public boolean isWorking() {
        return mIsWorking;
    }

    public CharSequence getGroupName() {
        return mGroupName;
    }

    public boolean isGroupAvailable() {
        return mGroupId != null && mGroupId.contains("null");
    }

    public SheduleActivityModel(SharedPreferences preferences, String cacheDir) {
        Log.i(TAG, "new Instance ");

        mGroupId = preferences.getString(String.valueOf(PreferensesConst.GROUP_ID), "null");
        mGroupName = preferences.getString(String.valueOf(PreferensesConst.GROUP_NAME), "null");
        mPeriod = (byte) preferences.getInt(String.valueOf(PreferensesConst.PERIOD), 0);

        mCacheDir = cacheDir + "/"+mGroupId;

        Log.i(TAG, "load period=" + mPeriod);

    }

    private boolean saveCache() {
        Log.d(TAG, "saveToFile");
        if (mShedule==null) return false;
        try {
            File f = new File(mCacheDir.toString());
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            Log.d(TAG, "begin save data to " + mCacheDir);

            if (mShedule != null) {
                oos.writeObject(mShedule);
                Log.d(TAG, "save in file");
            } else Log.d(TAG, "save file false");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "save file false");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "save file false");
            return false;
        }
        return true;
    }

    private boolean loadCache() {
        Log.d(TAG, "loadFromFile");
        try {
            File f = new File(mCacheDir.toString());

            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream oin = new ObjectInputStream(fis);

            Log.d(TAG, "begin load File " + mCacheDir);
            mShedule = (List<DayPairs>) oin.readObject();
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "load File fail");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "load File fail");
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public List<DayPairs> getShedule() {
        return mShedule;
    }

    public void loadFromCache() {
        if (loadCache()) mObservable.notifyLoadCacheOk();
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... arg) {
            Log.d(TAG, "doInBackground");

            try {
                Log.i(TAG, "load period=" + mPeriod);
                mShedule = TolgasModel.getSheduleByGroupId(mGroupId, mPeriod);
                if (mShedule == null) {
                    mShedule = new ArrayList<>();
                }
            } catch (IOException e) {
                e.printStackTrace();
                mShedule = null;
                return false;
            }
            saveCache();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mIsWorking = false;
            if (success) {
                mObservable.notifySucceeded();
            } else {
                mObservable.notifyFailed();
            }

        }
    }

    public void LoadData() {
        Log.i(TAG, "LoadData");

        if (mIsWorking) return;

        mObservable.notifyStarted();

        mIsWorking = true;
        mLoadTask = new LoadDataTask();
        mLoadTask.execute();
    }

    public void registerObserver(final Observer observer) {
        mObservable.registerObserver(observer);
        if (mIsWorking) observer.onLoadStarted(this);
    }

    public void unregisterObserver(final Observer observer) {
        mObservable.unregisterObserver(observer);
    }

    public void StopLoad() {
        Log.i(TAG, "StopLoad");
        if (mIsWorking) {
            mLoadTask.cancel(true);
            mIsWorking = false;
        }
    }

    public interface Observer {
        void onLoadStarted(SheduleActivityModel sheduleActivityModel);

        void onLoadFinished(SheduleActivityModel sheduleActivityModel);

        void onLoadFailed(SheduleActivityModel sheduleActivityModel);

        void onLoadCacheOk(SheduleActivityModel sheduleActivityModel);
    }

    private class LoadDataObservable extends Observable<Observer> {
        public void notifyLoadCacheOk() {
            for (final Observer observer : mObservers) {
                observer.onLoadCacheOk(SheduleActivityModel.this);
            }
        }

        public void notifyStarted() {
            for (final Observer observer : mObservers) {
                observer.onLoadStarted(SheduleActivityModel.this);
            }
        }

        public void notifySucceeded() {
            for (final Observer observer : mObservers) {
                observer.onLoadFinished(SheduleActivityModel.this);
            }
        }

        public void notifyFailed() {
            for (final Observer observer : mObservers) {
                observer.onLoadFailed(SheduleActivityModel.this);
            }
        }
    }

}
