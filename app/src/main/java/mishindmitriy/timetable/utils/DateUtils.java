package mishindmitriy.timetable.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mishindmitriy on 14.11.2015.
 */
public class DateUtils {
    public static String getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        int d = cal.get(Calendar.DAY_OF_WEEK);
        String dayOfWeek = "";
        switch (d) {
            case Calendar.MONDAY:
                dayOfWeek = "Понедельник";
                break;
            case Calendar.TUESDAY:
                dayOfWeek = "Вторник";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = "Среда";
                break;
            case Calendar.THURSDAY:
                dayOfWeek = "Четверг";
                break;
            case Calendar.FRIDAY:
                dayOfWeek = "Пятница";
                break;
            case Calendar.SATURDAY:
                dayOfWeek = "Суббота";
                break;
            case Calendar.SUNDAY:
                dayOfWeek = "Воскресенье";
                break;
        }
        return dayOfWeek;
    }
}
