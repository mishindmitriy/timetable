package mishindmitriy.timetable.model;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
public class Thing extends RealmObject implements Serializable {
    private String serverId;
    private String name;
    private String type;
    @PrimaryKey
    private long id;
    private int timesOpen = 0;
    private int rating;

    public String getName() {
        return name;
    }

    public Thing setName(String name) {
        this.name = name;
        return this;
    }

    public String getServerId() {
        return serverId;
    }

    public Thing setServerId(String serverId) {
        this.serverId = serverId;
        return this;
    }

    public int getTimesOpen() {
        return timesOpen;
    }

    public Thing setTimesOpen(int timesOpen) {
        this.timesOpen = timesOpen;
        return this;
    }

    public ThingType getType() {
        return ThingType.valueOf(type);
    }

    public Thing setType(ThingType type) {
        this.type = type.toString();
        rating = ThingType.getPositionByPeriod(type);
        return this;
    }

    public Thing setId() {
        id = (type + serverId).hashCode();
        return this;
    }

    public long getId() {
        return id;
    }

    public void incrementOpenTimes() {
        timesOpen++;
    }
}
