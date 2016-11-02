package mishindmitriy.timetable.model;

import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * Created by mishindmitriy on 12.11.2015.
 */
public enum ScheduleSubjectType {
    GROUP, TEACHER, CLASSROOM;

    private static HashMap<ScheduleSubjectType, Byte> positionByThingType = new HashMap<>();

    static {
        positionByThingType.put(ScheduleSubjectType.GROUP, (byte) 0);
        positionByThingType.put(ScheduleSubjectType.TEACHER, (byte) 1);
        positionByThingType.put(ScheduleSubjectType.CLASSROOM, (byte) 2);
    }

    public static int getPositionByPeriod(@NonNull ScheduleSubjectType type) {
        Byte index = positionByThingType.get(type);
        if (index == null) {
            throw new IllegalArgumentException("Wrong type argument");
        }
        return (int) index;
    }
}
