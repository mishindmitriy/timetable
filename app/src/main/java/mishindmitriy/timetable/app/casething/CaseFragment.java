package mishindmitriy.timetable.app.casething;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.shedule.SheduleActivity_;
import mishindmitriy.timetable.model.CaseThingModel;
import mishindmitriy.timetable.model.data.Thing;
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
    private ArrayAdapter<Thing> adapter;
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
        switch (mWhatCase) {
            case GROUP:
                filterText.setHint("Введите номер группы");
                break;
            case TEACHER:
                filterText.setHint("Введите фамилию преподавателя");
                break;
            case CLASSROOM:
                filterText.setHint("Введите номер аудитории");
                break;
        }
        setRetainInstance(true);
        this.mCaseModel = new CaseThingModel(mWhatCase);
        this.mCaseModel.registerObserver(this);
        if (mCaseModel.isWorking()) onLoadStarted();
        else this.mCaseModel.loadData();
        this.filterText.addTextChangedListener(this.filterTextWatcher);
    }

    @ItemClick(R.id.listView)
    protected void itemClick(Thing thing) {
        SheduleActivity_.intent(this.getActivity()).thing(thing).start();
    }

    @Override
    public void onLoadStarted() {
        this.mProgressBar.setVisibility(View.VISIBLE);
        this.mButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoadFinished(List<Thing> listThings) {
        this.adapter = new CaseAdapter(this.getActivity(), R.layout.item_thing, listThings);
        this.adapter.getFilter().filter(this.filterText.getText());
        listView.setAdapter(adapter);
        this.mProgressBar.setVisibility(View.INVISIBLE);
        if (adapter.getCount() == 0) this.mButton.setVisibility(View.VISIBLE);
        else this.mButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoadFailed() {
        if (this.mProgressBar != null) {
            this.mProgressBar.setVisibility(View.INVISIBLE);
        }
        if (this.mButton != null) {
            this.mButton.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.buttonRefresh)
    public void refreshClick() {
        this.mCaseModel.loadData();
    }

    @Override
    public void onDestroy() {
        this.mCaseModel.stopLoad();
        this.mCaseModel.unregisterObserver(this);
        super.onDestroy();
    }

    private class CaseAdapter extends ArrayAdapter<Thing> {
        public CaseAdapter(Context context, int resource, List<Thing> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView t = (TextView) super.getView(position, convertView, parent);
            if (getItem(position).isFavorite()) {
                Drawable grade;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    grade = getResources().getDrawable(R.drawable.ic_grade_white_48dp, getActivity().getTheme());
                    if (grade!=null) grade.setColorFilter(getResources().getColor(R.color.select), PorterDuff.Mode.MULTIPLY);
                } else {
                    grade = getResources().getDrawable(R.drawable.ic_grade_white_48dp);
                    if (grade!=null) grade.setColorFilter(getResources().getColor(R.color.select), PorterDuff.Mode.MULTIPLY);
                }
                t.setCompoundDrawablesWithIntrinsicBounds(grade,null,null,null);
            } else {
                t.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            }
            return t;
        }
    }
}
