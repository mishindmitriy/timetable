package mishindmitriy.timetable.app.shedule.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.model.data.entity.Thing;

/**
 * Created by mishindmitriy on 28.11.2015.
 */
@EViewGroup(R.layout.item_thing)
public class ViewItemFavoriteThing extends FrameLayout {
    @ViewById(R.id.grade)
    ImageView gradeImage;
    @ViewById(R.id.text)
    TextView nameTextView;
    private Thing thing;

    public ViewItemFavoriteThing(Context context) {
        super(context);
    }

    public ViewItemFavoriteThing(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewItemFavoriteThing(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void init() {
        gradeImage.setVisibility(GONE);
    }

    public void setThing(Thing thing) {
        if (thing == null || thing.equals(this.thing)) return;
        this.thing = thing;
        nameTextView.setText(thing.getName());
    }
}
