package mishindmitriy.timetable.app.shedule;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.joda.time.DateTime;

import javax.inject.Inject;

import mishindmitriy.timetable.app.TimeTableApp;
import mishindmitriy.timetable.utils.Prefs;

/**
 * Created by mishindmitriy on 08.01.2017.
 */
@InjectViewState
public class FeedbackPresenter extends MvpPresenter<FeedbackView> {
    @Inject
    protected Firebase firebase;
    @Inject
    protected Prefs prefs;

    public FeedbackPresenter() {
        TimeTableApp.component().inject(this);
    }

    public void checkNeedFeedbackAlert() {
        if (!prefs.isFeedbackShowed() && prefs.getUpdateDate().plusDays(7).isBeforeNow()) {
            getViewState().showFeedbackAlert();
        }
    }

    public void dismissFeedbackAlert() {
        getViewState().dismissFeedbackAlert();
    }

    public void sendFeedback(String androidId, String feedbackMessage) {
        getViewState().showLoadingAlert(true);
        firebase.child("feedback")
                .child(androidId)
                .child(String.valueOf(DateTime.now().getMillis()))
                .setValue(feedbackMessage, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        getViewState().showLoadingAlert(false);
                    }
                });
    }
}
