package mishindmitriy.timetable.model.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mishindmitriy on 14.11.2015.
 */
public class DateFormatter {
    public static final String patternDate = "dd.MM.yyyy";

    private static SimpleDateFormat getFormatter() {
        SimpleDateFormat simpleDateFormatter = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormatter.applyPattern(patternDate);
        return simpleDateFormatter;
    }

    public static Date parseDate(String date) {
        Date d;
        try {
            d = getFormatter().parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Illegal string with date");
        }
        return d;
    }

    public static String DateToString(Date date) {
        return getFormatter().format(date.getTime());
    }
}
