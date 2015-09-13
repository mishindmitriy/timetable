package mishindmitriy.timetable.TolgasModel;

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

/**
 * Created by mishindmitriy on 12.09.2015.
 * Adapter for shedule output in ListView
 */
public class SheduleListAdapter extends BaseAdapter {
    private List<DayPairs> shedule;
    private Activity mContext;

    public SheduleListAdapter(Activity context,List<DayPairs> shedule) {
        this.mContext =context;
        this.shedule=shedule;
    }

    static class ViewHolder {
        LinearLayout dayLayout;
        RelativeLayout pairLayout;
    }

    public void setData(List<DayPairs> shedule)
    {
        this.shedule=shedule;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return shedule.size();
    }

    @Override
    public Object getItem(int position) {
        return shedule.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= mContext.getLayoutInflater();
        ViewHolder viewHolder;
        int sizeOutput=shedule.get(position).getPairsArray().size();
        int sizeConvertView=0;

        if (convertView == null){
            convertView = inflater.inflate(R.layout.day_pairs_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.dayLayout = (LinearLayout) convertView.findViewById(R.id.dayPairsLayout);
            //viewHolder.pairLayout = (RelativeLayout) inflater.inflate(R.layout.pair, null, false);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            sizeConvertView=viewHolder.dayLayout.getChildCount()-1;
//            while (viewHolder.dayLayout.getChildCount()>1)
//            {
//                viewHolder.dayLayout.removeViewAt(1);
//            }
        }

        if (sizeConvertView<sizeOutput)
        {
            for (int k=sizeConvertView; k<sizeOutput; k++)
            {
                RelativeLayout pairLayout=(RelativeLayout) inflater.inflate(R.layout.pair, null, false);
                viewHolder.dayLayout.addView(pairLayout);
            }
        }
        else {
            if (sizeConvertView>sizeOutput)
            {
              for(int k=sizeConvertView;k>sizeOutput; k--)
              {
                  viewHolder.dayLayout.removeViewAt(k);
              }
            }
        }

        TextView viewDate = (TextView) viewHolder.dayLayout.findViewById(R.id.textViewDate);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(TolgasModel.formatDate);
        try {
            date = sdf.parse(shedule.get(position).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dayOfWeek = TolgasModel.getDayOfWeek(date);

        String today=sdf.format(new Date());
        String someDate=sdf.format(date);
        if (today.equals(someDate)&&someDate.equals(today))
        {
            TextView textView=(TextView) viewHolder.dayLayout.findViewById(R.id.today);
            textView.setVisibility(View.VISIBLE);
        }
        viewDate.setText(someDate);
        TextView viewDayOfWeek = (TextView) viewHolder.dayLayout.findViewById(R.id.textViewDayOfWeek);
        viewDayOfWeek.setText(dayOfWeek);

        for (int n = 0; n < shedule.get(position).getPairsArray().size(); n++) {
            RelativeLayout pairLayout=(RelativeLayout)viewHolder.dayLayout.getChildAt(n+1);
            //(RelativeLayout) inflater.inflate(R.layout.pair, null, false);

            TextView viewClassroom = (TextView) pairLayout.findViewById(R.id.textViewClassroom);
            TextView viewSubject = (TextView) pairLayout.findViewById(R.id.textViewSubject);
            TextView viewPrepod = (TextView) pairLayout.findViewById(R.id.textViewPrepod);
            TextView viewTypePair = (TextView) pairLayout.findViewById(R.id.textViewTypePair);
            TextView viewPairStart = (TextView) pairLayout.findViewById(R.id.textViewPairStart);
            TextView viewPairEnd = (TextView) pairLayout.findViewById(R.id.textViewPairEnd);

            viewClassroom.setText(shedule.get(position).getPair(n).getClassroom());
            viewSubject.setText(shedule.get(position).getPair(n).getSubject());
            viewPrepod.setText(shedule.get(position).getPair(n).getPrepod());
            viewTypePair.setText(shedule.get(position).getPair(n).getTypePair());

            int d = Integer.parseInt(shedule.get(position).getPair(n).getPairNumber());
            if (dayOfWeek.equals("Суббота")) {   //время пар в субботу
                switch (d) {
                    case 1:
                        viewPairStart.setText(TolgasModel.Saturday.firstPairStart);
                        viewPairEnd.setText(TolgasModel.Saturday.firstPairEnd);
                        break;
                    case 2:
                        viewPairStart.setText(TolgasModel.Saturday.secondPairStart);
                        viewPairEnd.setText(TolgasModel.Saturday.secondPairEnd);
                        break;
                    case 3:
                        viewPairStart.setText(TolgasModel.Saturday.thirdPairStart);
                        viewPairEnd.setText(TolgasModel.Saturday.thirdPairEnd);
                        break;
                    case 4:
                        viewPairStart.setText(TolgasModel.Saturday.fourthPairStart);
                        viewPairEnd.setText(TolgasModel.Saturday.fourthPairEnd);
                        break;
                    case 5:
                        viewPairStart.setText(TolgasModel.Saturday.fifthPairStart);
                        viewPairEnd.setText(TolgasModel.Saturday.fifthPairEnd);
                        break;
                }
            } else {
                switch (d) {

                    case 1:
                        viewPairStart.setText(TolgasModel.firstPairStart);
                        viewPairEnd.setText(TolgasModel.firstPairEnd);
                        break;
                    case 2:
                        viewPairStart.setText(TolgasModel.secondPairStart);
                        viewPairEnd.setText(TolgasModel.secondPairEnd);
                        break;
                    case 3:
                        viewPairStart.setText(TolgasModel.thirdPairStart);
                        viewPairEnd.setText(TolgasModel.thirdPairEnd);
                        break;
                    case 4:
                        viewPairStart.setText(TolgasModel.fourthPairStart);
                        viewPairEnd.setText(TolgasModel.fourthPairEnd);
                        break;
                    case 5:
                        viewPairStart.setText(TolgasModel.fifthPairStart);
                        viewPairEnd.setText(TolgasModel.fifthPairEnd);
                        break;
                    case 6:
                        viewPairStart.setText(TolgasModel.sixthPairStart);
                        viewPairEnd.setText(TolgasModel.sixthPairEnd);
                        break;
                    case 7:
                        viewPairStart.setText(TolgasModel.seventhPairStart);
                        viewPairEnd.setText(TolgasModel.seventhPairEnd);
                        break;
                }
            }
            //viewHolder.dayLayout.addView(pairLayout);
        }

        return convertView;
    }


}
