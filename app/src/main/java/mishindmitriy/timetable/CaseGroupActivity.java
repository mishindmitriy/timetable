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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.util.List;

import mishindmitriy.timetable.TolgasModel.CaseActivityModel;
import mishindmitriy.timetable.TolgasModel.Group;

public class CaseGroupActivity extends AppCompatActivity implements CaseActivityModel.Observer, OnItemClickListener {
    private List<Group> groups;
    private EditText filterText;
    private ArrayAdapter<String> adapter = null;
    private CaseActivityModel mCaseModel;
    private ProgressDialog pd;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_case_group);
        setTitle(R.string.caseGroup);

        filterText=(EditText) findViewById(R.id.editText);
        filterText.addTextChangedListener(filterTextWatcher);
        ListView listGroupView =(ListView) findViewById(R.id.listViewData);
        listGroupView.setOnItemClickListener(CaseGroupActivity.this);

        mCaseModel=new CaseActivityModel();
        mCaseModel.registerObserver(this);
        mCaseModel.LoadData();
    }

    @Override
    public void onLoadStarted(CaseActivityModel caseActivityModel) {
        if (pd!=null) pd.dismiss();
        pd = ProgressDialog
                .show(CaseGroupActivity.this,
                        getString(R.string.wait),
                        getString(R.string.connToServ), true, false);
    }

    @Override
    public void onLoadFinished(CaseActivityModel caseActivityModel) {
        ListView listview = (ListView) findViewById(R.id.listViewData);
        adapter=new ArrayAdapter<String>(CaseGroupActivity.this,
                android.R.layout.simple_list_item_1, caseActivityModel.getGroupNameList());
        listview.setAdapter(adapter);
        pd.dismiss();
    }

    @Override
    public void onLoadFailed(final CaseActivityModel caseActivityModel) {
        pd.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(CaseGroupActivity.this);
        builder.setTitle("Ошибка!")
                .setMessage("Загрузить данные не удалось. Проверьте интернет-соединение")
                .setCancelable(false)
                .setNegativeButton("Попробовать еще раз",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (pd!=null) pd.dismiss();
                                pd = ProgressDialog.show(CaseGroupActivity.this, getString(R.string.wait), getString(R.string.connToServ), true, false);
                                caseActivityModel.LoadData();
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //записываем выбранную группу в настройки
        SharedPreferences preferences = getSharedPreferences(String.valueOf(PreferensesConst.APP_PREFERENCES), Context.MODE_PRIVATE);
        mCaseModel.saveSelectGroup(preferences,position);
        Intent intent = new Intent(CaseGroupActivity.this, SheduleActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        filterText.removeTextChangedListener(filterTextWatcher);
        mCaseModel.unregisterObserver(this);
        if (isFinishing()) mCaseModel.StopLoad();
    }


}
