package mishindmitriy.timetable.model;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
public class ScheduleSubject extends RealmObject implements Serializable {
    private String serverId;
    private String name;
    private String type;
    @PrimaryKey
    private long id;
    private int timesOpen = 0;
    private int sortRating;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ScheduleSubject setName(String name) {
        this.name = name;
        return this;
    }

    public String getServerId() {
        return serverId;
    }

    public ScheduleSubject setServerId(String serverId) {
        this.serverId = serverId;
        return this;
    }

    public int getTimesOpen() {
        return timesOpen;
    }

    public ScheduleSubject setTimesOpen(int timesOpen) {
        this.timesOpen = timesOpen;
        return this;
    }

    public ScheduleSubjectType getEnumType() {
        return ScheduleSubjectType.valueOf(type);
    }

    public ScheduleSubject setEnumType(ScheduleSubjectType type) {
        this.type = type.toString();
        sortRating = ScheduleSubjectType.getPositionByPeriod(type);
        return this;
    }

    public ScheduleSubject setId() {
        id = Math.abs((type + serverId).hashCode());
        return this;
    }

    public long getId() {
        return id;
    }

    public void incrementOpenTimes() {
        timesOpen++;
    }

    public void mergeWithExistObject(Realm realm) {
        ScheduleSubject existObj = realm.where(ScheduleSubject.class)
                .beginGroup()
                .equalTo("serverId", serverId)
                .equalTo("type", type)
                .endGroup()
                .findFirst();
        if (existObj == null) return;
        timesOpen = existObj.getTimesOpen();
    }
}
