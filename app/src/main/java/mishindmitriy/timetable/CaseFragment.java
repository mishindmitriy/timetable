package mishindmitriy.timetable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import mishindmitriy.timetable.TolgasModel.CaseThingModel;

/**
 * Created by mishindmitriy on 14.09.2015.
 */
public class CaseFragment extends ListFragment
        implements CaseThingModel.Observer, Button.OnClickListener, AdapterView.OnItemClickListener {
    private final static String TAG="GroupsFragment ";

    private EditText filterText;
    private ArrayAdapter<String> adapter = null;
    private CaseThingModel mCaseModel;
    private final String mWhatCase;
    private ProgressBar mProgressBar;
    private Button mButton;

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (adapter!=null)
            {
                adapter.getFilter().filter(s);
            }
        }
    };

    public CaseFragment(String thatcase) {
        mWhatCase=thatcase;
        Log.i(TAG + mWhatCase, "Constructor");
        mCaseModel=new CaseThingModel(mWhatCase);
        mCaseModel.registerObserver(this);
        mCaseModel.LoadData();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG + mWhatCase, "onViewCreated");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG + mWhatCase, "onCreateView");
        View fragment=inflater.inflate(R.layout.fragment_case,null);

        filterText=(EditText) fragment.findViewById(R.id.editText);
        filterText.addTextChangedListener(filterTextWatcher);

        mButton=(Button)fragment.findViewById(R.id.buttonRefresh);
        mButton.setOnClickListener(this);

        mProgressBar=(ProgressBar)fragment.findViewById(R.id.progressBar);
        if (mCaseModel.isWorking())
        {
            mProgressBar.setVisibility(View.VISIBLE);
            mButton.setVisibility(View.GONE);
        }

        if (adapter!=null)
        {
            adapter.getFilter().filter(filterText.getText());
        }

        return fragment;
    }

    @Override
    public void onLoadStarted(CaseThingModel caseThingModel) {
        Log.i(TAG+mWhatCase,"onLoadStart");
        if (mProgressBar!=null)
        {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        if (mButton!=null)
        {
            mButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadFinished(CaseThingModel caseThingModel) {
        Log.i(TAG + mWhatCase, "onLoadFinished");
        adapter=new ArrayAdapter<>(getActivity(),R.layout.case_list_item, caseThingModel.getNameList());
        adapter.getFilter().filter(filterText.getText());
        setListAdapter(adapter);
        if (mProgressBar!=null)
        {
            mProgressBar.setVisibility(View.GONE);
        }
        if (mButton!=null)
        {
            mButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG + mWhatCase, "onViewCreated");

        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //записываем выбранную группу в настройки
        SharedPreferences preferences = getActivity().getSharedPreferences(String.valueOf(PreferensesConst.APP_PREFERENCES), Context.MODE_PRIVATE);
        TextView text=(TextView)view;
        mCaseModel.saveSelectThing(preferences, text.getText());
        Intent intent = new Intent(getActivity(), SheduleActivity.class);
        getActivity().finish();
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG + mWhatCase, "onDestroyView");
    }

    @Override
    public void onLoadFailed(CaseThingModel caseThingModel) {
        Log.i(TAG+mWhatCase, "onLoadFailed");
        if (mProgressBar!=null)
        {
            mProgressBar.setVisibility(View.GONE);
        }
        if (mButton!=null)
        {
            mButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG+mWhatCase,"onClick");
        mCaseModel.LoadData();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG+mWhatCase,"onDestroy");
        super.onDestroy();
        mCaseModel.StopLoad();
        mCaseModel.unregisterObserver(this);
    }
}
