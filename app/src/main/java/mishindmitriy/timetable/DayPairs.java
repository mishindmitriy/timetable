package mishindmitriy.timetable;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
public class DayPairs {
    private Calendar date;

    private List<Pair> arrayPairs;

    public DayPairs(Calendar date,@Nullable List<Pair> arrayPairs)
    {
        this.date=date;
        if (arrayPairs==null)
        {
            this.arrayPairs =new ArrayList<>();
        }
        else this.arrayPairs =arrayPairs;
    }

    public void addPair(Pair pair)
    {
        this.arrayPairs.add(pair);
    }
}
