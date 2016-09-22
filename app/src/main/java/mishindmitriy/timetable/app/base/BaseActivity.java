package mishindmitriy.timetable.app.base;

import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;

/**
 * Created by mishindmitriy on 02.07.2016.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Realm realm = Realm.getDefaultInstance();

    @Override
    protected void onDestroy() {
        if (realm != null) {
            realm.close();
            realm = null;
        }
        super.onDestroy();
    }

    public Realm getRealm() {
        return realm;
    }
}
