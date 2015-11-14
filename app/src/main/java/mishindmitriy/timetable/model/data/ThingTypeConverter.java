package mishindmitriy.timetable.model.data;

import java.util.HashMap;

/**
 * Created by mishindmitriy on 14.11.2015.
 */
public class ThingTypeConverter {
    private static HashMap<ThingType, Byte> positionByThingType;

    static {
        positionByThingType = new HashMap<>();
        //0 - запрос групп, 1 - запрос преподавателей, 2 - запрос по аудиториям
        positionByThingType.put(ThingType.GROUP, (byte) 0);
        positionByThingType.put(ThingType.TEACHER, (byte) 1);
        positionByThingType.put(ThingType.CLASSROOM, (byte) 2);
    }

    public static int getPositionByPeriod(ThingType type) {
        int pos = positionByThingType.get(type);
        if (pos < 0 || pos > 2) throw new IllegalArgumentException("Wrong argument");
        else return pos;
    }
}
