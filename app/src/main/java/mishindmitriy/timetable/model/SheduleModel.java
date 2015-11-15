package mishindmitriy.timetable.model;

import android.database.Observable;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import mishindmitriy.timetable.model.data.Pair;
import mishindmitriy.timetable.model.data.PeriodType;
import mishindmitriy.timetable.model.data.PeriodTypeConverter;
import mishindmitriy.timetable.model.data.Thing;
import mishindmitriy.timetable.model.data.ThingType;
import mishindmitriy.timetable.utils.ParseHelper;
import mishindmitriy.timetable.utils.PreferencesHelper;

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
    private List<Pair> mShedule= Collections.emptyList();

    public SheduleModel() {
        mPeriod=PreferencesHelper.getInstance().loadOutputPeriod();
    }

    public void setThing(Thing thing)
    {
        this.mThing = thing;
        if (this.mThing == null)
        {
            mThing = PreferencesHelper.getInstance().loadThing();
        } else PreferencesHelper.getInstance().saveThing(thing);
    }

    public PeriodType getPeriod() {
        return this.mPeriod;
    }

    public void setPeriod(final PeriodType period) {
        if (mPeriod==period) return;
        this.mPeriod = period;
        PreferencesHelper.getInstance().saveOutputPeriod(period);
        LoadData();
    }

    public void setPeriod(int position)
    {
        setPeriod(PeriodTypeConverter.getPeriodTypeByPosition(position));
    }

    public int getPeriodPosition() {
        return PeriodTypeConverter.getPositionByPeriod(mPeriod);
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

    public List<Pair> getShedule() {
        return this.mShedule;
    }

    public ThingType getWhatThing() {
        if (this.mThing != null) return this.mThing.getWhatThing();
        return null;
    }

    public void LoadData() {
        if (this.mIsWorking) return;
        if (!isThingAvailable()) return;

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

    private Date getToDate() {
        final Calendar cal = getFromCalendar();
        int offset = 0;
        switch (this.mPeriod) {
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
        return new Date(cal.getTimeInMillis());
    }

    private Calendar getFromCalendar()
    {
        final Calendar cal = Calendar.getInstance();
        switch (this.mPeriod) {
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
        return new Date(getFromCalendar().getTimeInMillis());
    }

    public interface Observer {
        void onLoadStarted();

        void onLoadFinished(List<Pair> shedule, boolean isCache);
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(final Void... arg) {
            try {
                SheduleModel.this.mShedule = ParseHelper.getShedule(mThing, SheduleModel.this.getFromDate(),SheduleModel.this.getToDate());
            } catch (final IOException e) {
                SheduleModel.this.mShedule = null;
                //SheduleModel.this.loadCache();
                //return false;
            }
            if (SheduleModel.this.mShedule == null) {
                SheduleModel.this.mShedule = new ArrayList<>();
            }
            //SheduleModel.this.saveCache();
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
                observer.onLoadFinished(SheduleModel.this.getShedule(),false);
            }
        }

        public void notifyFailed() {
            for (final Observer observer : this.mObservers) {
                observer.onLoadFinished(SheduleModel.this.getShedule(),true);
            }
        }
    }

}
