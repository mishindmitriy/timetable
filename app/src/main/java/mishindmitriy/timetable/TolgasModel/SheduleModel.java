package mishindmitriy.timetable.TolgasModel;

import android.content.SharedPreferences;
import android.database.Observable;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mishindmitriy.timetable.PreferensesConst;

/**
 * Created by mishindmitriy on 04.09.2015.
 */
public class SheduleModel {
    private static final String TAG = "SheduleModel";
    private boolean mIsWorking;
    private LoadDataTask mLoadTask;
    private final LoadDataObservable mObservable = new LoadDataObservable();

    public void setPeriod(byte mPeriod) {
        Log.i(TAG, "setPeriod=" + mPeriod);
        this.mPeriod = mPeriod;
        getDates();
    }

    private byte mPeriod;
    private Thing mThing=null;
    //private String mThingId=null;
   // private CharSequence mThingName=null;
    private List<DayPairs> mShedule;
    private String mCacheDir = null;
    private String[] dates;

    public byte getPeriod() {
        return mPeriod;
    }

    public boolean isWorking() {
        return mIsWorking;
    }

    public CharSequence getThingName() {
        if (mThing==null) return null;
        return mThing.getThingName();
    }

    public boolean isThingAvailable() {
        return mThing != null;
    }

    public SheduleModel(SharedPreferences preferences, String cacheDir) {
        Log.i(TAG, "new Instance ");

        mShedule=new ArrayList<>();

        String json=preferences.getString(PreferensesConst.CURRENT_THING,null);
        Gson gson=new Gson();
        mThing=gson.fromJson(json, Thing.class);

        if (mThing==null) return;
        //mThingId=thing.getThingID();
        //mThingName=thing.getThingName();
        //mThingId = preferences.getString(String.valueOf(PreferensesConst.GROUP_ID), "null");
        //mThingName = preferences.getString(String.valueOf(PreferensesConst.GROUP_NAME), "null");
        mPeriod = (byte) preferences.getInt(String.valueOf(PreferensesConst.PERIOD), 0);
        getDates();

        mCacheDir=cacheDir + "/" + mThing.getWhatThing()+"/" + mThing.getThingID() +"/";

        File cache = new File(mCacheDir);
        if (cache.mkdirs()) Log.i(TAG, mCacheDir + " dir created");
        else Log.i(TAG, mCacheDir + " dir not created");

        Log.i(TAG,"cache dir "+mCacheDir);
        Log.i(TAG, "load period=" + mPeriod);


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
        String json=new Gson().toJson(dayPairs);
        //Log.i(TAG,"json = "+json);
        String dir = mCacheDir+ dayPairs.getDate();
        Log.d(TAG, "saveToFile " + dir);
        try {
            File f = new File(dir);
            FileOutputStream fos = new FileOutputStream(f);
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fos);
            outputStreamWriter.write(json);
            Log.d(TAG, "save in file");
            outputStreamWriter.close();
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
            InputStreamReader inputStreamReader= new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            Gson gson=new Gson();
            dayPairs = gson.fromJson(reader,DayPairs.class);
            inputStreamReader.close();
        } catch (IOException e) {
            Log.d(TAG, "load File IOException fail");
            e.printStackTrace();
        }
        return dayPairs;
    }

    private String[] getDates()
    {
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
                if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) cal.roll(Calendar.DAY_OF_YEAR,-1);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                offset=6;
                break;
            case TolgasModel.NEXT_WEEK:
                if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) cal.roll(Calendar.DAY_OF_YEAR,5);
                else cal.roll(Calendar.DAY_OF_YEAR, 7);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                offset=6;
                break;
            case TolgasModel.THIS_MONTH:
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                offset=cal.getActualMaximum(Calendar.DAY_OF_MONTH)-1;
                break;
            case TolgasModel.NEXT_MONTH:
                cal.roll(Calendar.DAY_OF_YEAR,30);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                offset=cal.getActualMaximum(Calendar.DAY_OF_MONTH)-1;
                break;
        }
        dates = new String[offset+1];
        for (int n = 0; n <= offset; n++) {
            dates[n] = TolgasModel.getStringDate(cal);
            cal.roll(Calendar.DAY_OF_YEAR, 1);
        }
        return dates;
    }

    private boolean loadCache() {
        String[] dates=getDates();
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

    public String getWhatThing() {
        if (mThing!=null) return mThing.getWhatThing();
        return null;
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... arg) {
            Log.d(TAG, "doInBackground");

            try {
                Log.i(TAG, "load period=" + mPeriod);
                switch (mThing.getWhatThing())
                {
                    case TolgasModel.GROUPS:
                        mShedule = TolgasModel.getSheduleByGroupId(mThing.getThingID(), mPeriod, dates);
                        break;
                    case TolgasModel.PREDODS:
                        mShedule=TolgasModel.getSheduleByPrepodId(mThing.getThingID(),mPeriod,dates);
                        break;
                    case TolgasModel.CLASSROOMS:
                        mShedule=TolgasModel.getSheduleByClassroomId(mThing.getThingID(), mPeriod, dates);
                        break;

                }

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
        void onLoadStarted(SheduleModel sheduleModel);

        void onLoadFinished(SheduleModel sheduleModel);

        void onLoadFailed(SheduleModel sheduleModel);
    }

    private class LoadDataObservable extends Observable<Observer>  {

        public void notifyStarted() {
            for (final Observer observer : mObservers) {
                observer.onLoadStarted(SheduleModel.this);
            }
        }

        public void notifySucceeded() {
            for (final Observer observer : mObservers) {
                observer.onLoadFinished(SheduleModel.this);
            }
        }

        public void notifyFailed() {
            for (final Observer observer : mObservers) {
                observer.onLoadFailed(SheduleModel.this);
            }
        }
    }

}
