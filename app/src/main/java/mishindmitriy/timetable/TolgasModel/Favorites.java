package mishindmitriy.timetable.TolgasModel;

import java.util.List;

/**
 * Created by mishindmitriy on 15.09.2015.
 */
public class Favorites {
    private List<Thing> favorites;

    public Favorites(List<Thing> list)
    {
        favorites=list;
    }

    public List<Thing> getFavorites() {
        return favorites;
    }
}
