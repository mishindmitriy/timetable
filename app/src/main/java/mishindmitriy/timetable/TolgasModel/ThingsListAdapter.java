package mishindmitriy.timetable.TolgasModel;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import java.util.List;
import mishindmitriy.timetable.R;

/**
 * Created by mishindmitriy on 15.09.2015.
 */
public class ThingsListAdapter extends ArrayAdapter {
    private List<Thing> mThings;

    public ThingsListAdapter(Context context, int resource, List<Thing> things)
    {
        super(context, resource,things);
        mThings=things;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            Activity activity=(Activity) getContext();
            view = activity.getLayoutInflater().inflate(R.layout.case_list_item, parent, false);
        }
        CheckedTextView checkedTextView=(CheckedTextView) view.findViewById(android.R.id.text1);
        Thing thing=mThings.get(position);
        checkedTextView.setText(thing.getThingName());
        return view;
    }
}
