package mishindmitriy.timetable.TolgasModel;

import android.support.annotation.Nullable;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
public class Thing {
    private final String thingID;
    private final String thingName;
    private final String whatThing;

    public Thing(String thingID, String thingName, String thing) {
        this.thingID = thingID;
        this.thingName = thingName;
        this.whatThing=thing;
    }

    public String getThingID()
    {
       return thingID;
    }


    public String getThingName()
    {
        return thingName;
    }

    public String getWhatThing()
    {
        return whatThing;
    }

}
