package mishindmitriy.timetable.model.data;

import java.util.HashMap;

/**
 * Created by mishindmitriy on 12.11.2015.
 */
public class PeriodTypeConverter {
    private static HashMap<String,PeriodType> periodTypeByString;
    private static HashMap<Byte,PeriodType> periodTypeByPosition;
    private static HashMap<PeriodType,Byte> positionByPeriodType;

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
        PeriodType periodType=periodTypeByString.get(type);
        if (periodType==null) throw new IllegalArgumentException("Wrong argument");
        else return periodType;
    }

    public static int getPositionByPeriod(PeriodType type)
    {
        int pos=positionByPeriodType.get(type);
        if (pos<0 || pos>6) throw new IllegalArgumentException("Wrong argument");
        else return pos;
    }

    public static PeriodType getPeriodTypeByPosition(int pos)
    {
        PeriodType periodType=periodTypeByPosition.get((byte)pos);
        if (periodType==null) throw new IllegalArgumentException("Wrong argument");
        else return periodType;
    }
}
