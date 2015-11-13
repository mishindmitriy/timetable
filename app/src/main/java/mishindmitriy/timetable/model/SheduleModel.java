package mishindmitriy.timetable.model;

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

import mishindmitriy.timetable.model.data.DayPairs;
import mishindmitriy.timetable.model.data.PeriodType;
import mishindmitriy.timetable.model.data.PeriodTypeConverter;
import mishindmitriy.timetable.model.data.Thing;
import mishindmitriy.timetable.model.data.ThingType;
import mishindmitriy.timetable.utils.PreferencesHelper;
import mishindmitriy.timetable.utils.ParseHelper;

/**
 * Model for Shedule. Include all operation with shedule
 * Created by mishindmitriy on 04.09.2015.
 */
public class SheduleModel {
    private static final String TAG = "SheduleModel";
    private final LoadDataObservable mObservable = new LoadDataObservable();
    private Thing mThing=null;
    private boolean mIsWorking=false;
    private LoadDataTask mLoadTask=null;
    private PeriodType mPeriod;
    private List<DayPairs> mShedule=new ArrayList<>();
    private String mCacheDir=null;
    private String[] mDates=null;

    public SheduleModel() {
        Log.i(TAG, "new Instance ");
        mPeriod=PreferencesHelper.getInstance().loadOutputPeriod();
        if (mPeriod==null) mPeriod=PeriodType.TODAY;
    }

    private static DayPairs loadFile(final String fileName) {
        Log.d(TAG, "loadFromFile");
        DayPairs dayPairs = null;
        try {
            Log.i(TAG, "begin load file " + fileName);
            final File f = new File(fileName);
            final FileInputStream fis = new FileInputStream(f);
            final InputStreamReader inputStreamReader = new InputStreamReader(fis);
            final BufferedReader reader = new BufferedReader(inputStreamReader);
            final Gson gson = new Gson();
            dayPairs = gson.fromJson(reader, DayPairs.class);
            inputStreamReader.close();
        } catch (final IOException e) {
            Log.d(TAG, "load File IOException fail");
            e.printStackTrace();
        }
        return dayPairs;
    }

    public void init(final String cacheDir, Thing thing)
    {
        this.mThing = thing;
        if (this.mThing == null)
        {
            mThing = PreferencesHelper.getInstance().loadThing();
            if (mThing==null) return;
        } else PreferencesHelper.getInstance().saveThing(thing);

        this.getDates();
        this.mCacheDir = cacheDir + '/' + this.mThing.getWhatThing() + '/' + this.mThing.getThingID() + '/';
        final File cache = new File(this.mCacheDir);
        if (cache.mkdirs()) Log.i(TAG, this.mCacheDir + " dir created");
        else Log.i(TAG, this.mCacheDir + " dir not created");
        Log.i(TAG, "cache dir " + this.mCacheDir);
        Log.i(TAG, "load period=" + this.mPeriod);
        this.clearCache();
    }

    public PeriodType getPeriod() {
        return this.mPeriod;
    }

    public int getPeriodPosition() {
        return PeriodTypeConverter.getPositionByPeriod(mPeriod);
    }

    public void setPeriod(final PeriodType period) {
        Log.i(TAG, "setPeriod=" + period);
        this.mPeriod = period;
        PreferencesHelper.getInstance().saveOutputPeriod(period);
        this.getDates();
    }

    public boolean isWorking() {
        return this.mIsWorking;
    }

    public CharSequence getThingName() {
        if (this.mThing == null) return null;
        return this.mThing.getThingName();
    }

    public boolean isThingAvailable() {
        return this.mThing != null;
    }

    private void clearCache() {
        final Calendar cal = Calendar.getInstance();
        final File[] listFiles = new File(this.mCacheDir).listFiles();
        if (listFiles == null) {
            Log.i(TAG, "clearCache. listFiles return null");
            return;
        }
        for (final File cacheFile : listFiles) {
            Date cacheDate = null;
            final String name = cacheFile.getName();
            try {
                cacheDate = ParseHelper.getDate(name);
            } catch (final ParseException e) {
                e.printStackTrace();
                return;
            }
            final Date time = cal.getTime();
            final String stringDate = ParseHelper.getStringDate(cal);
            if (cacheDate.before(time) && !name.equals(stringDate) && !stringDate.equals(name)) {
                final String absolutePath = cacheFile.getAbsolutePath();
                Log.i(TAG, "file " + absolutePath + " will be deleted");
                cacheFile.delete();
            }
        }
    }

    private boolean saveCache(final DayPairs dayPairs) {
        final String json = new Gson().toJson(dayPairs);
        //Log.i(TAG,"json = "+json);
        final String dir = this.mCacheDir + dayPairs.getDate();
        Log.d(TAG, "saveToFile " + dir);
        try {
            final File f = new File(dir);
            final FileOutputStream fos = new FileOutputStream(f);
            final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(json);
            Log.d(TAG, "save in file");
            outputStreamWriter.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "save file false");
            return false;
        } catch (final IOException e) {
            e.printStackTrace();
            Log.d(TAG, "save file false ioexception");
            return false;
        }
        return true;
    }

    private boolean saveCache() {
        Log.i(TAG, "start save cache");
        for (int n = 0; n < this.mShedule.size(); n++) {
            this.saveCache(this.mShedule.get(n));
        }
        Log.i(TAG, "save cache end");
        return true;
    }

    private String[] getDates() {
        final Calendar cal = Calendar.getInstance();
        //надо сделать массив дат, на которые нужен кэш. какой выбран период?
        int offset = 0;
        switch (this.mPeriod) {
            case TODAY:
                break;
            case TOMORROW:
                cal.roll(Calendar.DAY_OF_YEAR, 1);
                break;
            case SEVEN_DAYS:
                offset = 6;
                break;
            case THIS_WEEK:
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    cal.roll(Calendar.DAY_OF_YEAR, -1);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                offset = 6;
                break;
            case NEXT_WEEK:
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    cal.roll(Calendar.DAY_OF_YEAR, 5);
                else cal.roll(Calendar.DAY_OF_YEAR, 7);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                offset = 6;
                break;
            case THIS_MONTH:
                final int actualMinimum = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DAY_OF_MONTH, actualMinimum);
                offset = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - 1;
                break;
            case NEXT_MONTH:
                cal.roll(Calendar.DAY_OF_YEAR, 30);
                final int actualMinimum1 = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DAY_OF_MONTH, actualMinimum1);
                offset = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - 1;
                break;
        }
        this.mDates = new String[offset + 1];
        for (int n = 0; n <= offset; n++) {
            this.mDates[n] = ParseHelper.getStringDate(cal);
            cal.roll(Calendar.DAY_OF_YEAR, 1);
        }
        return this.mDates;
    }

    private boolean loadCache() {
        final String[] dates = this.getDates();
        Log.i(TAG, "in cache need " + dates.length + " dates from " + this.mCacheDir);
        final File[] listFiles = new File(this.mCacheDir).listFiles();
        if (listFiles == null) {
            Log.i(TAG, "listFiles cache fail");
            return false;
        }
        Log.i(TAG, "cache dir is open");
        this.mShedule = new ArrayList<>();
        for (final String date : dates) {
            for (final File cacheFile : listFiles) {
                if (date.equals(cacheFile.getName()) && cacheFile.getName().equals(date)) {
                    final DayPairs day = SheduleModel.loadFile(cacheFile.getAbsolutePath());
                    this.mShedule.add(day);
                    Log.i(TAG, "date=" + date + "  file=" + cacheFile.getName());
                    break;
                }
            }
        }
        if (this.mShedule == null) {
            Log.i(TAG, "from cache load 0 files");
            return false;
        }
        if (this.mShedule.size() == 0) {
            Log.i(TAG, "from cache load 0 files");
            return false;
        }

        Log.i(TAG, "from cache load " + this.mShedule.size() + " days");

        return true;
    }

    public List<DayPairs> getShedule() {
        return this.mShedule;
    }

    public ThingType getWhatThing() {
        if (this.mThing != null) return this.mThing.getWhatThing();
        return null;
    }

    public void LoadData() {
        Log.i(TAG, "LoadData");

        if (this.mIsWorking) return;
        if (!isThingAvailable()) return;

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
        Log.i(TAG, "StopLoad");
        if (this.mIsWorking) {
            this.mLoadTask.cancel(true);
            this.mIsWorking = false;
        }
    }

    public interface Observer {
        void onLoadStarted(SheduleModel sheduleModel);

        void onLoadFinished(SheduleModel sheduleModel);

        void onLoadFailed(SheduleModel sheduleModel);
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(final Void... arg) {
            Log.d(TAG, "doInBackground");

            try {
                Log.i(TAG, "load period=" + SheduleModel.this.mPeriod);
                final String thingID = SheduleModel.this.mThing.getThingID();
                switch (SheduleModel.this.mThing.getWhatThing()) {
                    case GROUP:
                        SheduleModel.this.mShedule = ParseHelper.getSheduleByGroupId(thingID, SheduleModel.this.mDates);
                        break;
                    case TEACHER:
                        SheduleModel.this.mShedule = ParseHelper.getSheduleByTeacherId(thingID, SheduleModel.this.mDates);
                        break;
                    case CLASSROOM:
                        SheduleModel.this.mShedule = ParseHelper.getSheduleByClassroomId(thingID, SheduleModel.this.mDates);
                        break;
                }

                if (SheduleModel.this.mShedule == null) {
                    SheduleModel.this.mShedule = new ArrayList<>();
                }
                Log.i(TAG, "load period done");
            } catch (final IOException e) {
                e.printStackTrace();
                SheduleModel.this.mShedule = null;
                Log.i(TAG, "start load cache");
                SheduleModel.this.loadCache();
                Log.i(TAG, "load cache done");
                return false;
            }
            Log.i(TAG, "start save cache");
            SheduleModel.this.saveCache();
            Log.i(TAG, "save cache done");
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
                observer.onLoadStarted(SheduleModel.this);
            }
        }

        public void notifySucceeded() {
            for (final Observer observer : this.mObservers) {
                observer.onLoadFinished(SheduleModel.this);
            }
        }

        public void notifyFailed() {
            for (final Observer observer : this.mObservers) {
                observer.onLoadFailed(SheduleModel.this);
            }
        }
    }

}
