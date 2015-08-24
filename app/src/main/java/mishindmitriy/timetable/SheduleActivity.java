package mishindmitriy.timetable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SheduleActivity extends AppCompatActivity {


    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shedule);
        //чтение настроек из файла
        SharedPreferences preferences=getSharedPreferences(String.valueOf(PreferensesConst.APP_PREFERENCES), Context.MODE_PRIVATE);
        String group_id=preferences.getString(String.valueOf(PreferensesConst.GROUP_ID), "null");
        String group_name=preferences.getString(String.valueOf(PreferensesConst.GROUP_NUMBER),"null");

        //если в настройках нет записи, то запускаем активность со списком групп
        if (group_id != null && group_id.contains("null"))
        {
            Intent intent = new Intent(this,CaseGroupActivity.class);
            finish();
            startActivity(intent);
        }
        else {
            pd = ProgressDialog.show(this, getString(R.string.wait),getString(R.string.connToServ), true, false);
            setTitle(group_name);
            new ParseShedule().execute(group_id);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class ParseShedule extends AsyncTask<String, Void, List<DayPairs>> {

        protected List<DayPairs> doInBackground(String... arg) {
            List<DayPairs> list=new ArrayList<>();

            try
            {
                list=TolgasModel.getTodaySheduleByIdGroup(arg[0]);//передаем id группы
                if (list==null) {
                    return new ArrayList<>();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
            return list;
        }

        protected void onPostExecute(List<DayPairs> output) {
            pd.dismiss();
            if (output==null)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(SheduleActivity.this);
                builder.setTitle("Ошибка!")
                        .setMessage("Загрузить данные не удалось. Проверьте интернет-соединение")
                        .setCancelable(false)
                        .setNegativeButton("Попробовать еще раз",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        SharedPreferences preferences = getSharedPreferences(String.valueOf(PreferensesConst.APP_PREFERENCES), Context.MODE_PRIVATE);
                                        String group_id = preferences.getString(String.valueOf(PreferensesConst.GROUP_ID), "0");
                                        pd = ProgressDialog.show(SheduleActivity.this, getString(R.string.wait),getString(R.string.connToServ), true, false);
                                        new ParseShedule().execute(group_id);
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                return;
            }
            //ListView listView = (ListView) findViewById(R.id.listViewData);
            //нужен новый адаптер под List<Daypairs>
            //listView.setAdapter(new SheduleArrayAdapter(SheduleActivity.this,
            //        R.layout.day_pairs_layout, output));

            LinearLayout dayslayout=(LinearLayout) findViewById(R.id.dayPairsLayoutOnActivity);
            LayoutInflater layoutInflater=getLayoutInflater();
            final String TAG="output pairs";
            for(int i=0; i<output.size();i++)
            {

                LinearLayout dayLayout= (LinearLayout) layoutInflater.inflate(R.layout.day_pairs_layout, null, false);
                TextView viewDate= (TextView) dayLayout.findViewById(R.id.textViewDate);

                viewDate.setText(output.get(i).getDate().toString());
                dayslayout.addView(dayLayout);
                for (int n=0; n<output.get(i).getPairsArray().size(); n++)
                {
                    RelativeLayout pairLayout=(RelativeLayout) layoutInflater.inflate(R.layout.pair, null, false);
                    TextView viewClassroom= (TextView) pairLayout.findViewById(R.id.textViewClassroom);
                    viewClassroom.setText(output.get(i).getPair(n).getClassroom());
                    TextView viewSubject= (TextView) pairLayout.findViewById(R.id.textViewSubject);
                    viewSubject.setText(output.get(i).getPair(n).getSubject());
                    dayLayout.addView(pairLayout);
                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shedule, menu);
        //SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
              //  R.array.action_shedule_list, android.R.layout.simple_spinner_dropdown_item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_caseGroup:
                Intent intent = new Intent(this,CaseGroupActivity.class);
                finish();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
