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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CaseGroupActivity extends AppCompatActivity {
    private List<String> groupNameList;
    private List<Group> groups;
    private ListView listGroupView;
    private ProgressDialog pd;
    private EditText filterText;
    private ArrayAdapter<String> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_group);
        setTitle(R.string.caseGroup);
        pd = ProgressDialog.show(this, getString(R.string.wait),getString(R.string.connToServ), true, false);
        new ParseGroups().execute();
        listGroupView =(ListView) findViewById(R.id.listViewData);
        listGroupView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //записываем выбранную группу в настройки
                String groupName=adapter.getItem(position);
                int i=0;
                while (!groups.get(i).getGroupName().equals(groupName)) i++;
                SharedPreferences preferences = getSharedPreferences(String.valueOf(PreferensesConst.APP_PREFERENCES), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(String.valueOf(PreferensesConst.GROUP_ID), groups.get(i).getGroupID()); // groupList.get(0).get(pos)
                editor.putString(String.valueOf(PreferensesConst.GROUP_NUMBER), groups.get(i).getGroupName());
                editor.apply();
                Intent intent = new Intent(CaseGroupActivity.this, SheduleActivity.class);
                finish();
                startActivity(intent);
            }
        });
        filterText=(EditText) findViewById(R.id.editText);
        filterText.addTextChangedListener(filterTextWatcher);
    }

       private class ParseGroups extends AsyncTask<String, Void, List<Group>> {
         protected List<Group> doInBackground(String... arg) {
            return TolgasModel.getGroupsList();
        }

        protected void onPostExecute(List<Group> output) {
            if (output==null)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(CaseGroupActivity.this);
                builder.setTitle("Ошибка!")
                        .setMessage("Загрузить данные не удалось. Проверьте интернет-соединение")
                        .setCancelable(false)
                        .setNegativeButton("Попробовать еще раз",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        pd = ProgressDialog.show(CaseGroupActivity.this, getString(R.string.wait),getString(R.string.connToServ), true, false);
                                        new ParseGroups().execute();
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                return;
            }
            pd.dismiss();
            ListView listview = (ListView) findViewById(R.id.listViewData);
            groups=output;
            Iterator<Group> i=groups.iterator();
            groupNameList=new ArrayList<>();
            while (i.hasNext())
            {
                Group g=i.next();
                groupNameList.add(g.getGroupName());
            }
            adapter=new ArrayAdapter<String>(CaseGroupActivity.this,
                    android.R.layout.simple_list_item_1, groupNameList);
            listview.setAdapter(adapter);
        }
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            adapter.getFilter().filter(s);
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        filterText.removeTextChangedListener(filterTextWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_case_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_caseGroup) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
