package mishindmitriy.timetable.app.casething;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.shedule.SheduleActivity_;
import mishindmitriy.timetable.model.CaseThingModel;
import mishindmitriy.timetable.model.data.ThingType;

/**
 * Created by mishindmitriy on 14.09.2015.
 */
@EFragment(R.layout.fragment_case)
public class CaseFragment extends Fragment implements CaseThingModel.Observer {
    private static final String TAG = "GroupsFragment ";

    @FragmentArg
    protected ThingType mWhatCase;

    @ViewById(R.id.editText)
    protected EditText filterText;
    @ViewById(R.id.buttonRefresh)
    protected Button mButton;
    @ViewById(R.id.progressBar)
    protected ProgressBar mProgressBar;
    @ViewById(R.id.listView)
    protected ListView listView;

    private CaseThingModel mCaseModel;
    private ArrayAdapter<String> adapter;
    private final TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (CaseFragment.this.adapter != null) {
                CaseFragment.this.adapter.getFilter().filter(s);
            }
        }
    };

    @AfterViews
    public void init() {
        switch (mWhatCase)
        {
            case GROUP: filterText.setHint("Введите номер группы");
                break;
            case TEACHER:filterText.setHint("Введите фамилию преподавателя");
                break;
            case CLASSROOM: filterText.setHint("Введите номер аудитории");
                break;
        }
        setRetainInstance(true);
        this.mCaseModel = new CaseThingModel(mWhatCase);
        this.mCaseModel.registerObserver(this);
        if (mCaseModel.isWorking()) onLoadStarted(mCaseModel);
        else this.mCaseModel.LoadData();
        this.filterText.addTextChangedListener(this.filterTextWatcher);
    }

    @ItemClick(R.id.listView)
    protected void itemClick(int position) {
        SheduleActivity_.intent(this.getActivity()).thing(mCaseModel.getList().get(position)).start();
    }

    @Override
    public void onLoadStarted(CaseThingModel caseThingModel) {
        this.mProgressBar.setVisibility(View.VISIBLE);
        this.mButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoadFinished(CaseThingModel caseThingModel) {
        this.adapter = new ArrayAdapter<>(this.getActivity(), R.layout.case_list_item, caseThingModel.getNameList());
        this.adapter.getFilter().filter(this.filterText.getText());
        listView.setAdapter(adapter);
        this.mProgressBar.setVisibility(View.INVISIBLE);
        if (adapter.getCount() == 0) this.mButton.setVisibility(View.VISIBLE);
        else this.mButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoadFailed(CaseThingModel caseThingModel) {
        if (this.mProgressBar != null) {
            this.mProgressBar.setVisibility(View.INVISIBLE);
        }
        if (this.mButton != null) {
            this.mButton.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.buttonRefresh)
    public void refreshClick() {
        this.mCaseModel.LoadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mCaseModel.StopLoad();
        this.mCaseModel.unregisterObserver(this);
    }
}