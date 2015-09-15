package mishindmitriy.timetable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import mishindmitriy.timetable.TolgasModel.TolgasModel;

/**
 * Created by mishindmitriy on 14.09.2015.
 */
public class ThingsPagerAdapter extends FragmentPagerAdapter {
    private final static String TAG="ThingsPagerAdapter";

    private FragmentManager fm;

    public ThingsPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm=fm;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "getItem");

        String thing=TolgasModel.GROUPS;
        switch (position)
        {
            case 1:
                thing=TolgasModel.PREDODS;
                break;
            case 2:
                thing= TolgasModel.CLASSROOMS;
                break;
        }
        return new CaseFragment(thing);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0: return "Группы";
            case 1: return "Преподаватели";
            case 2: return "Аудитории";
        }
        return "wtf";
    }
}
