package mishindmitriy.timetable;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
public class Pair {
    private String classroom;
    private String pairNumber;
    private String prepod;
    private String typePair;
    private String subject;

    public Pair(String classroom,String pairNumber,String prepod,String typePair, String subject)
    {
        this.classroom=classroom;
        this.pairNumber=pairNumber;
        this.prepod=prepod;
        this.typePair=typePair;
        this.subject=subject;
    }
}
