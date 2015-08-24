package mishindmitriy.timetable;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by mishindmitriy on 08.06.2015
 */
public class SheduleArrayAdapter extends ArrayAdapter<DayPairs>
{
    private final static String TAG="SheduleArrayAdapter";

    public SheduleArrayAdapter(Context context, int resource, List<DayPairs> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DayPairs day = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.day_pairs_layout,null);
        }
        Log.d(TAG, day.getDate() + day.getPairsArray().size());
        ((TextView) convertView.findViewById(R.id.textViewDate))
                .setText(day.getDate());
        //((ListView) convertView.findViewById(R.id.listViewPairs))
        //        .setAdapter(new PairAdapter(getContext(), R.layout.pair, day.getPairsArray()));
        LinearLayout l=(LinearLayout) convertView.findViewById(R.id.dayPairsLayout);
        //View child = l.getLayoutInflater().inflate(R.layout.pair);
        //тут просто добавлять textView
        View child=null;
        for (int i=0; i<day.getPairsArray().size(); i++)
        {
            child = LayoutInflater.from(getContext())
                    .inflate(R.layout.pair, null);
            Log.d(TAG, "add " + i + " child");
            ((TextView) child.findViewById(R.id.textViewClassroom))
                    .setText(day.getPair(i).getClassroom());
            Log.d(TAG, "setTExt classroom" + day.getPair(i).getClassroom());
            ((TextView) child.findViewById(R.id.textViewSubject))
                    .setText(day.getPair(i).getSubject());
            Log.d(TAG, "setTExt subject" + day.getPair(i).getSubject());
            if (child!=null) l.addView(child);
            else Log.d(TAG,"fail, "+i+" child is null");
        }

        Log.d(TAG, "view hi=" + convertView.getHeight());
        Log.d(TAG, "parent hi=" + parent.getHeight());
        //Log.d(TAG,"listview hi="+((ListView) convertView.findViewById(R.id.listViewPairs)).getHeight());
        return convertView;
    }


}