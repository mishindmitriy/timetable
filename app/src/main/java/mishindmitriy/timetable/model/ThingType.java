package mishindmitriy.timetable.model;

import java.util.HashMap;

/**
 * Created by mishindmitriy on 12.11.2015.
 */
public enum ThingType {
    GROUP, TEACHER, CLASSROOM;

    private static HashMap<ThingType, Byte> positionByThingType= new HashMap<>();
    static {
        positionByThingType.put(ThingType.GROUP, (byte) 0);
        positionByThingType.put(ThingType.TEACHER, (byte) 1);
        positionByThingType.put(ThingType.CLASSROOM, (byte) 2);
    }

    public static int getPositionByPeriod(ThingType type) {
        if (type==null) throw new IllegalArgumentException("Wrong argument");
        return (int) positionByThingType.get(type);
    }
}
