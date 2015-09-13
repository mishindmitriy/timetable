package mishindmitriy.timetable.TolgasModel;

import android.content.SharedPreferences;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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

        mCacheDir=cacheDir + "/" + TolgasModel.GROUPS+"/" + mGroupId+"/";
        File cache = new File(mCacheDir);
        if (cache.mkdirs()) Log.i(TAG, mCacheDir + " dir created");
        else Log.i(TAG, mCacheDir + " dir not created");

        Log.i(TAG,"cache dir "+mCacheDir);
        Log.i(TAG, "load period=" + mPeriod);

        mShedule=new ArrayList<>();
        clearCache();
    }

    private void clearCache() {
        Calendar cal = Calendar.getInstance();
        File[] listFiles=new File(mCacheDir).listFiles();
        if (listFiles==null)
        {
            Log.i(TAG,"clearCache. listFiles return null");
            return;
        }
        for (File cacheFile : listFiles) {
            Date cacheDate = null;
            try {
                cacheDate = TolgasModel.getDate(cacheFile.getName());
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            if (cacheDate.before(cal.getTime()) && !(cacheFile.getName().equals(TolgasModel.getStringDate(cal))) && !(TolgasModel.getStringDate(cal).equals(cacheFile.getName()))) {
                Log.i(TAG, "file " + cacheFile.getAbsolutePath() + " will be deleted");
                cacheFile.delete();
            }
        }
    }

    private boolean saveCache(DayPairs dayPairs) {
        String dir = mCacheDir+ dayPairs.getDate();
        Log.d(TAG, "saveToFile "+dir);
        try {
            File f = new File(dir);
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dayPairs);
            Log.d(TAG, "save in file");
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "save file false");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "save file false ioexception");
            return false;
        }
        return true;
    }

    private boolean saveCache() {
        Log.i(TAG, "start save cache");
        for (int n = 0; n < mShedule.size(); n++) {
            saveCache(mShedule.get(n));
        }
        Log.i(TAG, "save cache end");
        return true;
    }


    private DayPairs loadFile(String fileName) {
        Log.d(TAG, "loadFromFile");
        DayPairs dayPairs = null;
        try {
            Log.i(TAG, "begin load file " + fileName);
            File f = new File(fileName);

            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream oin = new ObjectInputStream(fis);

            dayPairs = (DayPairs) oin.readObject();
            oin.close();
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "load File ClassNotFoundException fail");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "load File IOException fail");
            e.printStackTrace();
        }
        return dayPairs;
    }


    private boolean loadCache() {
        Calendar cal = Calendar.getInstance();
        //надо сделать массив дат, на которые нужен кэш. какой выбран период?
        int offset = 0;
        switch (mPeriod) {
            case TolgasModel.TODAY:
                break;
            case TolgasModel.TOMORROW:
                cal.roll(Calendar.DAY_OF_YEAR, 1);
                break;
            case TolgasModel.SEVEN_DAYS:
                offset = 6;
                break;
            case TolgasModel.THIS_WEEK:
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                offset=6;
                break;
            case TolgasModel.NEXT_WEEK:
                cal.roll(Calendar.DAY_OF_YEAR,6);
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                offset=6;
                break;
            case TolgasModel.THIS_MONTH:
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                offset=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                break;
            case TolgasModel.NEXT_MONTH:
                cal.roll(Calendar.DAY_OF_YEAR,30);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                offset=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                break;
        }
        String[] dates = new String[offset+1];
        for (int n = 0; n <= offset; n++) {
            dates[n] = TolgasModel.getStringDate(cal);
            cal.roll(Calendar.DAY_OF_YEAR, 1);
        }
        Log.i(TAG,"in cache need "+dates.length+" dates from "+mCacheDir);
        File[] listFiles = new File(mCacheDir).listFiles();
        if (listFiles==null)
        {
            Log.i(TAG,"listFiles cache fail");
            return false;
        }
        Log.i(TAG, "cache dir is open");
        mShedule=new ArrayList<>();
        for (String date : dates) {
            for (File cacheFile : listFiles) {
                if (date.equals(cacheFile.getName()) && cacheFile.getName().equals(date)) {
                    DayPairs day=loadFile(cacheFile.getAbsolutePath());
                    mShedule.add(day);
                    Log.i(TAG, "date=" + date + "  file=" + cacheFile.getName());
                    break;
                }
            }
        }
        if (mShedule == null) {
            Log.i(TAG, "from cache load 0 files");
            return false;
        }
        if (mShedule.size()==0)
        {
            Log.i(TAG, "from cache load 0 files");
            return false;
        }

        Log.i(TAG, "from cache load " + mShedule.size() + " days");

        return true;
    }

    public List<DayPairs> getShedule() {
        return mShedule;
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
                Log.i(TAG,"load period done");
            } catch (IOException e) {
                e.printStackTrace();
                mShedule = null;
                Log.i(TAG,"start load cache");
                loadCache();
                Log.i(TAG, "load cache done");
                return false;
            }
            Log.i(TAG,"start save cache");
            saveCache();
            Log.i(TAG, "save cache done");
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
    }

    private class LoadDataObservable extends Observable<Observer>  {

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
