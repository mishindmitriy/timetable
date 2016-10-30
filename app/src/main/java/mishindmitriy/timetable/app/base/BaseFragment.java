package mishindmitriy.timetable.app.base;

import android.support.v4.app.Fragment;

import io.realm.Realm;


/**
 * Created by mishindmitriy on 19.09.2016.
 */
public abstract class BaseFragment extends Fragment {
    protected Realm realm = Realm.getDefaultInstance();

    @Override
    public void onDestroy() {
        if (realm != null) {
            realm.close();
            realm = null;
        }
        super.onDestroy();
    }
}
