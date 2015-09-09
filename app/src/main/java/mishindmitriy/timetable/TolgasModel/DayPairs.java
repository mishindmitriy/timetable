package mishindmitriy.timetable.TolgasModel;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
public class DayPairs implements Serializable {
    private String date;

    private List<Pair> arrayPairs;

    public DayPairs(String date,@Nullable List<Pair> arrayPairs)
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

    public String getDate()
    {
        return date;
    }

    public List<Pair> getPairsArray()
    {
        return arrayPairs;
    }

    @Nullable
    public Pair getPair(int position)
    {
        if (position>arrayPairs.size()) return null;
        return arrayPairs.get(position);
    }
}
