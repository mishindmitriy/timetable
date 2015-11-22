package mishindmitriy.timetable.model.data;

import java.util.HashMap;

/**
 * Created by mishindmitriy on 14.11.2015.
 */
public class ThingTypeConverter {
    private static HashMap<ThingType, Byte> positionByThingType= new HashMap<>();
    static {
        positionByThingType.put(ThingType.GROUP, (byte) 0);
        positionByThingType.put(ThingType.TEACHER, (byte) 1);
        positionByThingType.put(ThingType.CLASSROOM, (byte) 2);
    }

    public static int getPositionByPeriod(ThingType type) {
        if (type==null) throw new IllegalArgumentException("Wrong argument");
        int pos = positionByThingType.get(type);
        return pos;
    }
}
