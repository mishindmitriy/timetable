package mishindmitriy.timetable.model.data;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mishindmitriy on 05.06.2015.
 * Class for Days, which have a pairs
 */
@Deprecated
public class DayPairs implements Serializable {
    private final String date;

    private final List<Pair> arrayPairs;

    public DayPairs(String date, @Nullable List<Pair> arrayPairs) {
        this.date = date;
        if (arrayPairs == null) {
            this.arrayPairs = new ArrayList<>();
        } else this.arrayPairs = arrayPairs;
    }

    public void addPair(Pair pair) {
        if (arrayPairs!=null) this.arrayPairs.add(pair);
    }

    public String getDate() {
        return this.date;
    }

    public List<Pair> getPairsArray() {
        return this.arrayPairs;
    }

    @Nullable
    public Pair getPair(int position) {
        if (position > this.arrayPairs.size()) return null;
        return this.arrayPairs.get(position);
    }
}
