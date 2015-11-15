package mishindmitriy.timetable.model.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
public class Pair implements Serializable {
    private String classroom;
    private Byte number;
    private String teacher;
    private String type;
    private String subject;
    private String groups;
    private Date date;
    private String note;

    public Pair(){

    }

    public Pair(String classroom, Byte number, String teacher, String type, String subject, String groups, Date date) {
        this.classroom = classroom;
        this.number = number;
        this.teacher = teacher;
        this.type = type;
        this.subject = subject;
        this.groups = groups;
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getClassroom() {
        return this.classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public Byte getNumber() {
        return this.number;
    }

    public void setNumber(Byte number) {
        this.number = number;
    }

    public String getTeacher() {
        return this.teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
