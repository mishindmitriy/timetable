package mishindmitriy.timetable.app;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mishindmitriy on 03.01.2017.
 */

@Module
public class AndroidModule {
    private final Application application;

    public AndroidModule(Application application) {
        this.application = application;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ApplicationContext @Annotation} to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ApplicationContext
    public Context provideApplicationContext() {
        return application;
    }
}
