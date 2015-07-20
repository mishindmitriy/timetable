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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class SheduleActivity extends AppCompatActivity {


    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shedule);
        //чтение настроек из файла
        SharedPreferences preferences=getSharedPreferences(String.valueOf(PreferensesConst.APP_PREFERENCES), Context.MODE_PRIVATE);
        String group_id=preferences.getString(String.valueOf(PreferensesConst.GROUP_ID), "0");
        String group_name=preferences.getString(String.valueOf(PreferensesConst.GROUP_NUMBER),"");

        //если в настройках нет записи, то запускаем активность со списком групп
        if (group_id != null && group_id.contains("0"))
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

    private class ParseShedule extends AsyncTask<String, Void, List<String>> {

        protected List<String> doInBackground(String... arg) {
            List<String> list=null;
            list=TolgasModel.getTodaySheduleByIdGroup(arg[0]);//передаем id группы
            return list;
        }

        protected void onPostExecute(List<String> output) {
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
            ListView listView = (ListView) findViewById(R.id.listViewData);
            //нужен новый адаптер под List<Daypairs>
            listView.setAdapter(new ArrayAdapter<>(SheduleActivity.this,
                    android.R.layout.simple_list_item_1, output));

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
