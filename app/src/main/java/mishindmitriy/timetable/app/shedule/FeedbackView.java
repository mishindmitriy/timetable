package mishindmitriy.timetable.app.shedule;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by mishindmitriy on 08.01.2017.
 */

public interface FeedbackView extends MvpView {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void showFeedbackAlert();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void dismissFeedbackAlert();
}
