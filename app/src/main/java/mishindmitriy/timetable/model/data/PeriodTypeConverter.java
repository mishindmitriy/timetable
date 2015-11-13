package mishindmitriy.timetable.model.data;

import java.util.HashMap;

/**
 * Created by mishindmitriy on 12.11.2015.
 */
public class PeriodTypeConverter {
    private static HashMap<String,PeriodType> periodTypeByString;
    static
    {
        periodTypeByString =new HashMap<>();
        periodTypeByString.put(PeriodType.TODAY.toString(), PeriodType.TODAY);
        periodTypeByString.put(PeriodType.TOMORROW.toString(), PeriodType.TOMORROW);
        periodTypeByString.put(PeriodType.NEXT_MONTH.toString(), PeriodType.NEXT_MONTH);
        periodTypeByString.put(PeriodType.NEXT_WEEK.toString(), PeriodType.NEXT_WEEK);
        periodTypeByString.put(PeriodType.THIS_MONTH.toString(), PeriodType.THIS_MONTH);
        periodTypeByString.put(PeriodType.THIS_WEEK.toString(), PeriodType.THIS_WEEK);
        periodTypeByString.put(PeriodType.SEVEN_DAYS.toString(), PeriodType.SEVEN_DAYS);
    }

    private static HashMap<Byte,PeriodType> periodTypeByPosition;
    static
    {
        periodTypeByPosition =new HashMap<>();
        periodTypeByPosition.put((byte)0, PeriodType.TODAY);
        periodTypeByPosition.put((byte)1, PeriodType.TOMORROW);
        periodTypeByPosition.put((byte)6, PeriodType.NEXT_MONTH);
        periodTypeByPosition.put((byte)4, PeriodType.NEXT_WEEK);
        periodTypeByPosition.put((byte)5, PeriodType.THIS_MONTH);
        periodTypeByPosition.put((byte)3, PeriodType.THIS_WEEK);
        periodTypeByPosition.put((byte)2, PeriodType.SEVEN_DAYS);
    }

    private static HashMap<PeriodType,Byte> positionByPeriodType;
    static
    {
        positionByPeriodType =new HashMap<>();
        positionByPeriodType.put(PeriodType.TODAY,(byte)0);
        positionByPeriodType.put(PeriodType.TOMORROW,(byte)1);
        positionByPeriodType.put(PeriodType.NEXT_MONTH,(byte)6);
        positionByPeriodType.put(PeriodType.NEXT_WEEK,(byte)4);
        positionByPeriodType.put(PeriodType.THIS_MONTH,(byte)5);
        positionByPeriodType.put(PeriodType.THIS_WEEK,(byte)3);
        positionByPeriodType.put(PeriodType.SEVEN_DAYS,(byte)2 );
    }

    public static PeriodType getPeriodTypeFromString(String type)
    {
        return periodTypeByString.get(type);
    }

    public static int getPositionByPeriod(PeriodType type)
    {
        return positionByPeriodType.get(type);
    }

    public static PeriodType getPeriodTypeByPosition(int pos)
    {
        return periodTypeByPosition.get((byte)pos);
    }
}
