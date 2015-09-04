package mishindmitriy.timetable.TolgasModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.database.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mishindmitriy.timetable.PreferensesConst;

/**
 * Created by mishindmitriy on 04.09.2015.
 */
public class CaseActivityModel {
    private static final String TAG ="CaseActivityModel";
    private boolean mIsWorking;
    private LoadDataTask mLoadTask;
    private final LoadDataObservable mObservable=new LoadDataObservable();
    private List<Group> mGroups;

    public List<String> getGroupNameList()
    {
        Iterator<Group> i=mGroups.iterator();
        List<String> groupNameList=new ArrayList<>();
        while (i.hasNext())
        {
            Group g=i.next();
            groupNameList.add(g.getGroupName());
        }
        return groupNameList;
    }

    public void saveSelectGroup(SharedPreferences preferences,int position)
    {
        //записываем выбранную группу в настройки
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(String.valueOf(PreferensesConst.GROUP_ID), mGroups.get(position).getGroupID());
        editor.putString(String.valueOf(PreferensesConst.GROUP_NAME), mGroups.get(position).getGroupName());
        editor.apply();
    }

    public CaseActivityModel() {
        Log.i(TAG, "new Instance");
    }

    private class LoadDataTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(final Void... params)
        {
            try {
                mGroups=TolgasModel.getGroupsList();
            } catch(IOException e){
                e.printStackTrace();
                return false;
            }
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

    public void LoadData(){
        if (mIsWorking) return;

        mObservable.notifyStarted();

        mIsWorking=true;
        mLoadTask=new LoadDataTask();
        mLoadTask.execute();
    }

    public void registerObserver(final Observer observer)
    {
        mObservable.registerObserver(observer);
        if (mIsWorking) observer.onLoadStarted(this);
    }

    public void unregisterObserver(final Observer observer)
    {
        mObservable.unregisterObserver(observer);
    }

    public void StopLoad(){
        if (mIsWorking){
            mLoadTask.cancel(true);
            mIsWorking=false;
        }
    }

    public interface Observer {
        void onLoadStarted(CaseActivityModel caseActivityModel);

        void onLoadFinished(CaseActivityModel caseActivityModel);

        void onLoadFailed(CaseActivityModel caseActivityModel);
    }

    private class LoadDataObservable extends Observable<Observer>
    {
        public void notifyStarted()
        {
            for (final Observer observer : mObservers){
                observer.onLoadStarted(CaseActivityModel.this);
            }
        }

        public void notifySucceeded()
        {
            for (final Observer observer : mObservers){
                observer.onLoadFinished(CaseActivityModel.this);
            }
        }

        public void notifyFailed()
        {
            for (final Observer observer : mObservers){
                observer.onLoadFailed(CaseActivityModel.this);
            }
        }
    }

}
