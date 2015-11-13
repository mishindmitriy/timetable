package mishindmitriy.timetable.model.data;

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
    private String date;

    public Pair(){

    }

    public Pair(String classroom, String pairNumber, String prepod, String typePair, String subject, String groups, String date) {
        this.classroom = classroom;
        this.pairNumber = pairNumber;
        this.prepod = prepod;
        this.typePair = typePair;
        this.subject = subject;
        this.groups = groups;
        this.date = date;
    }

    public String getClassroom() {
        return this.classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getPairNumber() {
        return this.pairNumber;
    }

    public void setPairNumber(String pairNumber) {
        this.pairNumber = pairNumber;
    }

    public String getPrepod() {
        return this.prepod;
    }

    public void setPrepod(String prepod) {
        this.prepod = prepod;
    }

    public String getTypePair() {
        return this.typePair;
    }

    public void setTypePair(String typePair) {
        this.typePair = typePair;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGroups() {
        return this.groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
