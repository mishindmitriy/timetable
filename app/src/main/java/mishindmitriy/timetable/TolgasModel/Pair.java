package mishindmitriy.timetable.TolgasModel;

import java.io.Serializable;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
public class Pair implements Serializable {
    private String classroom;
    private String pairNumber;
    private String prepod;
    private String typePair;
    private String subject;
    private String groups;

    public Pair(String classroom,String pairNumber,String prepod,String typePair, String subject, String groups)
    {
        this.classroom=classroom;
        this.pairNumber=pairNumber;
        this.prepod=prepod;
        this.typePair=typePair;
        this.subject=subject;
        this.groups=groups;
    }

    public String getClassroom()
    {
        return classroom;
    }

    public String getPairNumber()
    {
        return pairNumber;
    }

    public String getPrepod()
    {
        return prepod;
    }

    public String getTypePair()
    {
        return typePair;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getGroups()
    {
        return groups;
    }

}
