package mishindmitriy.timetable.model;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by mishindmitriy on 18.09.2015.
 */
public class SheduleWorkerFragment extends Fragment {
    private SheduleModel mSheduleModel;

    public SheduleWorkerFragment() {
        super();
        this.mSheduleModel = new SheduleModel();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public SheduleModel getSheduleModel() {
        return this.mSheduleModel;
    }
}
