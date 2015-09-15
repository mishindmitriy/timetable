package mishindmitriy.timetable.TolgasModel;

import android.content.SharedPreferences;
import android.database.Observable;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mishindmitriy.timetable.PreferensesConst;

/**
 * Created by mishindmitriy on 04.09.2015.
 */
public class CaseThingModel {
    private static final String TAG ="CaseModel";
    private boolean mIsWorking;
    public LoadDataTask mLoadTask;
    private final LoadDataObservable mObservable=new LoadDataObservable();
    private List<Thing> mListThingCases=new ArrayList<>();
    private final String mWhatCase;

    public List<String> getNameList()
    {
        Iterator<Thing> i= mListThingCases.iterator();
        List<String> groupNameList=new ArrayList<>();
        while (i.hasNext())
        {
            Thing g=i.next();
            groupNameList.add(g.getThingName());
        }
        return groupNameList;
    }

    public int getPositionByName(CharSequence groupName)
    {
        int pos=0;
        Iterator iterator= mListThingCases.iterator();
        while (!mListThingCases.get(pos).getThingName().equals(groupName))
        {
            iterator.next();
            pos++;
        }
        return pos;
    }



    public void saveSelectThing(SharedPreferences preferences,CharSequence groupName)
    {
        String json=preferences.getString(PreferensesConst.FAVORITES,null);
        List<Thing> favorites=new ArrayList<>();
        Favorites favoritesThing=new Favorites(favorites);
        Gson gson=new Gson();
        int position=getPositionByName(groupName);
        Thing currentThing=mListThingCases.get(position);

        if (json!=null)
        {
            favoritesThing=gson.fromJson(json, Favorites.class);
        }
        favoritesThing.getFavorites().add(currentThing);



        SharedPreferences.Editor editor = preferences.edit();
        //json=gson.toJson(favoritesThing);
        //editor.putString(PreferensesConst.FAVORITES,json);

        //записываем выбранную группу в настройки
        editor.putString(PreferensesConst.CURRENT_THING,gson.toJson(currentThing));
        //editor.putString(String.valueOf(PreferensesConst.GROUP_ID), mListThingCases.get(position).getThingID());
        //editor.putString(String.valueOf(PreferensesConst.GROUP_NAME), mListThingCases.get(position).getThingName());
        editor.apply();
    }

    public CaseThingModel(String things) {
        Log.i(TAG, "new Instance");
        mWhatCase =things;
    }

    public boolean isWorking() {
        return mIsWorking;
    }

    public List<Thing> getList() {
        return mListThingCases;
    }

    private class LoadDataTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(final Void... params)
        {
            try {
                switch (mWhatCase)
                {
                    case TolgasModel.GROUPS:
                        mListThingCases =TolgasModel.getGroupsList();
                        break;
                    case TolgasModel.PREDODS:
                        mListThingCases =TolgasModel.getPrepodsList();
                        break;
                    case TolgasModel.CLASSROOMS:
                        mListThingCases =TolgasModel.getClassroomList();
                        break;
                }

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
        void onLoadStarted(CaseThingModel caseGroupModel);

        void onLoadFinished(CaseThingModel caseGroupModel);

        void onLoadFailed(CaseThingModel caseGroupModel);
    }

    private class LoadDataObservable extends Observable<Observer>
    {
        public void notifyStarted()
        {
            for (final Observer observer : mObservers){
                observer.onLoadStarted(CaseThingModel.this);
            }
        }

        public void notifySucceeded()
        {
            for (final Observer observer : mObservers){
                observer.onLoadFinished(CaseThingModel.this);
            }
        }

        public void notifyFailed()
        {
            for (final Observer observer : mObservers){
                observer.onLoadFailed(CaseThingModel.this);
            }
        }
    }

}
