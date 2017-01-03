package mishindmitriy.timetable.app;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

/**
 * Created by mishindmitriy on 03.01.2017.
 */
@Module
public class RealmModule {
    @Provides
    public Realm provideRealm() {
        return Realm.getDefaultInstance();
    }
}
