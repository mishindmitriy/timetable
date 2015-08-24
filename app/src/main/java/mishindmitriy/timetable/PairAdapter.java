package mishindmitriy.timetable;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mishindmitriy on 18.06.2015.
 */
public class PairAdapter extends ArrayAdapter<Pair> {
    private final static String TAG = "PairAdapter";

    public PairAdapter(Context context, int resource, List<Pair> objects) {
        super(context, resource, objects);
        //dfsdafasdfasdg;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Pair pair = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.pair, null);
        }
        Log.d(TAG, pair.getClassroom() + pair.getSubject());
        ((TextView) convertView.findViewById(R.id.textViewClassroom))
                .setText(pair.getClassroom());
        ((TextView) convertView.findViewById(R.id.textViewSubject))
                .setText(pair.getSubject());
        //setText(day.getPairsArray().toString());
        Log.d(TAG, "parent hi=" + parent.getHeight());
        Log.d(TAG, "view hi=" + convertView.getHeight());
        return convertView;
    }
}
