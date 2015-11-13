package mishindmitriy.timetable.model.data;

import java.io.Serializable;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
public class Pair implements Serializable {
    private final String classroom;
    private final String pairNumber;
    private final String prepod;
    private final String typePair;
    private final String subject;
    private final String groups;
    //private final Date date; TODO

    public Pair(String classroom, String pairNumber, String prepod, String typePair, String subject, String groups) {
        this.classroom = classroom;
        this.pairNumber = pairNumber;
        this.prepod = prepod;
        this.typePair = typePair;
        this.subject = subject;
        this.groups = groups;
    }

    public String getClassroom() {
        return this.classroom;
    }

    public String getPairNumber() {
        return this.pairNumber;
    }

    public String getPrepod() {
        return this.prepod;
    }

    public String getTypePair() {
        return this.typePair;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getGroups() {
        return this.groups;
    }

}
