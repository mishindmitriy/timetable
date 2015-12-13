package mishindmitriy.timetable.app.casething;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mishindmitriy.timetable.BuildConfig;
import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.ObjectAdapter;
import mishindmitriy.timetable.app.casething.widget.ViewItemThing;
import mishindmitriy.timetable.app.casething.widget.ViewItemThing_;
import mishindmitriy.timetable.model.CaseThingModel;
import mishindmitriy.timetable.model.data.ThingType;
import mishindmitriy.timetable.model.data.entity.Thing;
import mishindmitriy.timetable.model.db.HelperFactory;

/**
 * Created by mishindmitriy on 14.09.2015.
 */
@EFragment(R.layout.fragment_case)
public class CaseFragment extends Fragment implements CaseThingModel.Observer {
    @FragmentArg
    protected ThingType mWhatCase;
    @ViewById(R.id.editText)
    protected EditText filterEditText;
    @ViewById(R.id.buttonRefresh)
    protected Button mButton;
    @ViewById(R.id.progressBar)
    protected ProgressBar mProgressBar;
    @ViewById(R.id.listView)
    protected ListView listView;

    private CaseThingModel mCaseModel;
    private CaseAdapter adapter;
    private final TextWatcher textWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (adapter != null) {
                adapter.getFilter().filter(s);
            }
        }
    };

    @AfterViews
    public void init() {
        adapter = new CaseAdapter();
        listView.setAdapter(adapter);
        switch (mWhatCase) {
            case GROUP:
                filterEditText.setHint(R.string.input_group);
                break;
            case TEACHER:
                filterEditText.setHint(R.string.input_teacher);
                break;
            case CLASSROOM:
                filterEditText.setHint(R.string.input_classroom);
                break;
        }
        mCaseModel = new CaseThingModel(mWhatCase);
        mCaseModel.registerObserver(this);
        if (mCaseModel.isWorking()) onLoadStarted();
        else {
            mCaseModel.loadData();
        }
        filterEditText.addTextChangedListener(textWatcher);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Thing thing = adapter.getItem(position);
                ViewItemThing v = (ViewItemThing) view;
                boolean newState = !thing.isFavorite();
                thing.setFavorite(newState);
                v.setThing(thing);
                thingUpdate(thing);
                if (!BuildConfig.DEBUG) {
                    if (newState) {
                        Answers.getInstance().logCustom(new CustomEvent("Добавили в закладки")
                                .putCustomAttribute("Группа", thing.toString()));
                    } else {
                        Answers.getInstance().logCustom(new CustomEvent("Убрали из закладок")
                                .putCustomAttribute("Группа", thing.toString()));
                    }
                }
            }
        });

    }

    @Background
    void thingUpdate(Thing thing) {
        try {
            HelperFactory.getInstance().getThingGAO().update(thing);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CaseActivity activity = (CaseActivity) getActivity();
        activity.forwardUpdate();
    }

    @Override
    public void onLoadStarted() {
        this.mProgressBar.setVisibility(View.VISIBLE);
        this.mButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoadFinished(List<Thing> listThings) {
        if (listThings == null || listThings.size() == 0) {
            mButton.setVisibility(View.VISIBLE);
            return;
        } else mButton.setVisibility(View.INVISIBLE);
        setThingList(listThings);
    }

    private void setThingList(List<Thing> listThings) {
        if (listThings == null || listThings.size() == 0) return;
        mProgressBar.setVisibility(View.INVISIBLE);
        adapter.getFilter().filter(filterEditText.getText());
        int index = listView.getFirstVisiblePosition();
        View v = listView.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());
        adapter.setData(listThings);
        listView.setSelectionFromTop(index, top);
    }

    @Override
    public void onCacheLoad(List<Thing> listThings) {
        setThingList(listThings);
    }

    @Override
    public void onLoadFailed() {
        if (listView.getCount() == 0) {
            if (this.mProgressBar != null) {
                this.mProgressBar.setVisibility(View.INVISIBLE);
            }
            if (this.mButton != null) {
                this.mButton.setVisibility(View.VISIBLE);
            }
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

    private class CaseAdapter extends ObjectAdapter<Thing> implements Filterable {
        private List<Thing> originalObjects;
        private ArrayFilter mFilter;

        public CaseAdapter(List<Thing> list) {
            super(list);
        }

        public CaseAdapter() {
            super();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ViewItemThing_.build(parent.getContext());
            }
            ViewItemThing view = (ViewItemThing) convertView;
            view.setThing(getItem(position));
            return view;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new ArrayFilter();
            }
            return mFilter;
        }

        private class ArrayFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();

                if (originalObjects == null) {
                    originalObjects = new ArrayList<>(objects);
                }

                if (prefix == null || prefix.length() == 0) {
                    ArrayList<Thing> list = new ArrayList<>(originalObjects);

                    results.values = list;
                    results.count = list.size();
                } else {
                    String prefixString = prefix.toString().toLowerCase();

                    ArrayList<Thing> values = new ArrayList<>(originalObjects);

                    final int count = values.size();
                    final ArrayList<Thing> newValues = new ArrayList<>();

                    for (int i = 0; i < count; i++) {
                        final Thing value = values.get(i);
                        final String valueText = value.toString().toLowerCase();

                        // First match against the whole, non-splitted value
                        if (valueText.startsWith(prefixString)) {
                            newValues.add(value);
                        } else {
                            final String[] words = valueText.split(" ");
                            final int wordCount = words.length;

                            // Start at index 0, in case valueText starts with space(s)
                            for (int k = 0; k < wordCount; k++) {
                                if (words[k].startsWith(prefixString)) {
                                    newValues.add(value);
                                    break;
                                }
                            }
                        }
                    }

                    results.values = newValues;
                    results.count = newValues.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                objects = (List<Thing>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }
}
