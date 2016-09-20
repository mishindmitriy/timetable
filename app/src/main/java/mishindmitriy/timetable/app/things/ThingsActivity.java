package mishindmitriy.timetable.app.things;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import io.realm.Realm;
import mishindmitriy.timetable.R;
import mishindmitriy.timetable.app.base.BaseActivity;
import mishindmitriy.timetable.app.base.BaseAdapter;
import mishindmitriy.timetable.app.shedule.SheduleActivity_;
import mishindmitriy.timetable.model.Thing;
import mishindmitriy.timetable.model.ThingType;
import mishindmitriy.timetable.utils.DataHelper;
import mishindmitriy.timetable.utils.Prefs;

@EActivity(R.layout.activity_things)
public class ThingsActivity extends BaseActivity {
    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;
    @ViewById(R.id.searchView)
    protected SearchView searchView;
    @ViewById(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @ViewById(R.id.recyclerView)
    protected RecyclerView recyclerView;
    private ThingAdapter thingAdapter = new ThingAdapter();

    @AfterViews
    protected void init() {
        if (Prefs.get().getSelectedThingServerId() != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        searchView.onActionViewExpanded();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(thingAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                thingAdapter.filter(newText);
                return false;
            }
        });
        thingAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<Thing>() {
            @Override
            public void onItemClick(Thing thing) {
                final String serverId = thing.getServerId();
                Prefs.get().setSelectedThingServerId(serverId);
                SheduleActivity_.intent(ThingsActivity.this).start();
                finish();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Thing currentThing = realm.where(Thing.class)
                                .equalTo("serverId", Prefs.get().getSelectedThingServerId())
                                .findFirst();
                        currentThing.incrementOpenTimes();
                    }
                });
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadThings();
            }
        });
        loadThings();
    }

    private void loadThings() {
        swipeRefreshLayout.setRefreshing(true);
        for (final ThingType thingType : ThingType.values()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        List<Thing> thingList = DataHelper.getSomeThing(thingType);
                        HashSet<String> serverIds = new HashSet<String>();
                        for (Thing t : thingList) {
                            Thing existThing = realm.where(Thing.class)
                                    .equalTo("serverId", t.getServerId())
                                    .findFirst();
                            if (existThing != null) {
                                t.setTimesOpen(existThing.getTimesOpen());
                            }
                            serverIds.add(t.getServerId());
                        }
                        realm.copyToRealmOrUpdate(thingList);

                        for (Thing t : realm.where(Thing.class)
                                .equalTo("type", thingType.toString())
                                .findAll()) {
                            if (!serverIds.contains(t.getServerId())) {
                                t.deleteFromRealm();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        }
    }
}
