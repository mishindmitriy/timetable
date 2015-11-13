package mishindmitriy.timetable.model.data;

import java.io.Serializable;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
public class Thing implements Serializable {
    private final String thingID;
    private final String thingName;
    private final ThingType whatThing;

    public Thing(String thingID, String thingName, ThingType thing) {
        this.thingID = thingID;
        this.thingName = thingName;
        this.whatThing = thing;
    }

    public String getThingID() {
        return this.thingID;
    }


    public String getThingName() {
        return this.thingName;
    }

    public ThingType getWhatThing() {
        return this.whatThing;
    }

}
