package mishindmitriy.timetable.app.things.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.model.Thing;

/**
 * Created by mishindmitriy on 28.11.2015.
 */
@EViewGroup(R.layout.item_thing)
public class ViewItemThing extends FrameLayout {
    @ViewById(R.id.text)
    TextView nameTextView;

    public ViewItemThing(Context context) {
        super(context);
    }

    public ViewItemThing(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewItemThing(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void init() {

    }

    public void setThing(Thing thing) {
        nameTextView.setText(thing.getName());
    }
}
