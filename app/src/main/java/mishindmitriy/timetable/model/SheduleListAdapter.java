package mishindmitriy.timetable.model;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.model.data.DayPairs;
import mishindmitriy.timetable.model.data.ThingType;
import mishindmitriy.timetable.utils.ParseHelper;

/**
 * Created by mishindmitriy on 12.09.2015.
 * Adapter for shedule output in ListView
 */
public class SheduleListAdapter extends BaseAdapter {
    private final Activity mContext;
    private List<DayPairs> shedule;
    private ThingType mWhatThing;

    public SheduleListAdapter(Activity context, List<DayPairs> shedule, ThingType whatThing) {
        this.mContext = context;
        this.shedule = shedule;
        this.mWhatThing = whatThing;
    }

    public void setData(List<DayPairs> shedule, ThingType thing) {
        this.shedule = shedule;
        this.mWhatThing=thing;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.shedule.size();
    }

    @Override
    public Object getItem(int position) {
        return this.shedule.get(position);
    }

    @Override
    public long getItemId(int position) {
        return shedule.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.mContext.getLayoutInflater();
        ViewHolder viewHolder;
        int sizeOutput = this.shedule.get(position).getPairsArray().size();
        int sizeConvertView = 0;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.day_pairs_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.dayLayout = (LinearLayout) convertView.findViewById(R.id.dayPairsLayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            sizeConvertView = viewHolder.dayLayout.getChildCount() - 1;
        }

        if (sizeConvertView < sizeOutput) {
            for (int k = sizeConvertView; k < sizeOutput; k++) {
                RelativeLayout pairLayout = (RelativeLayout) inflater.inflate(R.layout.pair, null, false);
                viewHolder.dayLayout.addView(pairLayout);
            }
        } else {
            if (sizeConvertView > sizeOutput) {
                for (int k = sizeConvertView; k > sizeOutput; k--) {
                    viewHolder.dayLayout.removeViewAt(k);
                }
            }
        }

        TextView viewDate = (TextView) viewHolder.dayLayout.findViewById(R.id.textViewDate);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(ParseHelper.formatDate);
        try {
            date = sdf.parse(this.shedule.get(position).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dayOfWeek = ParseHelper.getDayOfWeek(date);

        String today = sdf.format(new Date());
        String someDate = sdf.format(date);
        TextView textView = (TextView) viewHolder.dayLayout.findViewById(R.id.today);
        if (today.equals(someDate) && someDate.equals(today)) {
            textView.setVisibility(View.VISIBLE);
        } else textView.setVisibility(View.GONE);
        viewDate.setText(someDate);
        TextView viewDayOfWeek = (TextView) viewHolder.dayLayout.findViewById(R.id.textViewDayOfWeek);
        viewDayOfWeek.setText(dayOfWeek);

        for (int n = 0; n < this.shedule.get(position).getPairsArray().size(); n++) {
            RelativeLayout pairLayout = (RelativeLayout) viewHolder.dayLayout.getChildAt(n + 1);
            //(RelativeLayout) inflater.inflate(R.layout.pair, null, false);

            TextView viewClassroom = (TextView) pairLayout.findViewById(R.id.textViewClassroom);
            TextView viewSubject = (TextView) pairLayout.findViewById(R.id.textViewSubject);
            TextView viewPrepod = (TextView) pairLayout.findViewById(R.id.textViewPrepod);
            TextView viewTypePair = (TextView) pairLayout.findViewById(R.id.textViewTypePair);
            TextView viewPairStart = (TextView) pairLayout.findViewById(R.id.textViewPairStart);
            TextView viewPairEnd = (TextView) pairLayout.findViewById(R.id.textViewPairEnd);

            switch (this.mWhatThing) {
                case GROUP:
                    viewClassroom.setText(this.shedule.get(position).getPair(n).getClassroom());
                    viewSubject.setText(this.shedule.get(position).getPair(n).getSubject());
                    viewPrepod.setText(this.shedule.get(position).getPair(n).getPrepod());
                    viewTypePair.setText(this.shedule.get(position).getPair(n).getTypePair());
                    break;
                case TEACHER:
                    viewClassroom.setText(this.shedule.get(position).getPair(n).getClassroom());
                    viewSubject.setText(this.shedule.get(position).getPair(n).getSubject());
                    viewPrepod.setText(this.shedule.get(position).getPair(n).getGroups());
                    viewTypePair.setText(this.shedule.get(position).getPair(n).getTypePair());
                    break;
                case CLASSROOM:
                    viewClassroom.setText(this.shedule.get(position).getPair(n).getGroups());
                    viewSubject.setText(this.shedule.get(position).getPair(n).getSubject());
                    viewPrepod.setText(this.shedule.get(position).getPair(n).getPrepod());
                    viewTypePair.setText(this.shedule.get(position).getPair(n).getTypePair());
                    break;
            }
            int d=0;
            try {
                d = Integer.parseInt(this.shedule.get(position).getPair(n).getPairNumber(),10);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
            if (dayOfWeek.equals("Суббота")) {   //время пар в субботу
                switch (d) {
                    case 1:
                        viewPairStart.setText(ParseHelper.Saturday.firstPairStart);
                        viewPairEnd.setText(ParseHelper.Saturday.firstPairEnd);
                        break;
                    case 2:
                        viewPairStart.setText(ParseHelper.Saturday.secondPairStart);
                        viewPairEnd.setText(ParseHelper.Saturday.secondPairEnd);
                        break;
                    case 3:
                        viewPairStart.setText(ParseHelper.Saturday.thirdPairStart);
                        viewPairEnd.setText(ParseHelper.Saturday.thirdPairEnd);
                        break;
                    case 4:
                        viewPairStart.setText(ParseHelper.Saturday.fourthPairStart);
                        viewPairEnd.setText(ParseHelper.Saturday.fourthPairEnd);
                        break;
                    case 5:
                        viewPairStart.setText(ParseHelper.Saturday.fifthPairStart);
                        viewPairEnd.setText(ParseHelper.Saturday.fifthPairEnd);
                        break;
                }
            } else {
                switch (d) {

                    case 1:
                        viewPairStart.setText(ParseHelper.firstPairStart);
                        viewPairEnd.setText(ParseHelper.firstPairEnd);
                        break;
                    case 2:
                        viewPairStart.setText(ParseHelper.secondPairStart);
                        viewPairEnd.setText(ParseHelper.secondPairEnd);
                        break;
                    case 3:
                        viewPairStart.setText(ParseHelper.thirdPairStart);
                        viewPairEnd.setText(ParseHelper.thirdPairEnd);
                        break;
                    case 4:
                        viewPairStart.setText(ParseHelper.fourthPairStart);
                        viewPairEnd.setText(ParseHelper.fourthPairEnd);
                        break;
                    case 5:
                        viewPairStart.setText(ParseHelper.fifthPairStart);
                        viewPairEnd.setText(ParseHelper.fifthPairEnd);
                        break;
                    case 6:
                        viewPairStart.setText(ParseHelper.sixthPairStart);
                        viewPairEnd.setText(ParseHelper.sixthPairEnd);
                        break;
                    case 7:
                        viewPairStart.setText(ParseHelper.seventhPairStart);
                        viewPairEnd.setText(ParseHelper.seventhPairEnd);
                        break;
                }
            }
            //viewHolder.dayLayout.addView(pairLayout);
        }

        return convertView;
    }

    static class ViewHolder {
        LinearLayout dayLayout;
    }


}
