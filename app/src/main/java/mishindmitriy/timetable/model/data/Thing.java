package mishindmitriy.timetable.model.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
@DatabaseTable
public class Thing implements Serializable {
    @DatabaseField(dataType = DataType.STRING, unique = true)
    private String serverId;
    @DatabaseField(dataType = DataType.STRING)
    private String name;
    @DatabaseField(dataType = DataType.ENUM_STRING, index = true)
    private ThingType type;
    @DatabaseField(generatedId = true)
    private long id;

    public Thing()
    {

    }

    public Thing(String serverId, String name, ThingType thing) {
        this.serverId = serverId;
        this.name = name;
        this.type = thing;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServerId() {
        return this.serverId;
    }

    @Override
    public String toString() {
        if (name != null) return name;
        else return super.toString();
    }

    public String getName() {
        return this.name;
    }

    public ThingType getType() {
        return this.type;
    }

}
