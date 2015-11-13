package mishindmitriy.timetable.app.casething;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import mishindmitriy.timetable.model.data.ThingType;

/**
 * Created by mishindmitriy on 14.09.2015.
 */
public class ThingsPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "ThingsPagerAdapter";

    public ThingsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "getItem");

        switch (position) {
            case 0: return CaseFragment_.builder().mWhatCase(ThingType.GROUP).build();
            case 1: return CaseFragment_.builder().mWhatCase(ThingType.TEACHER).build();
            case 2: return CaseFragment_.builder().mWhatCase(ThingType.CLASSROOM).build();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Группы";
            case 1:
                return "Преподаватели";
            case 2:
                return "Аудитории";
            default:
                return "wtf";
        }
    }
}
