package mishindmitriy.timetable.model;

import org.joda.time.LocalDate;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
public class Pair extends RealmObject implements Serializable {
    @PrimaryKey
    private long id;
    private String classroom;
    private Byte number;
    private String teacher;
    private String type;
    private String subject;
    private String group;
    private String date;
    private String note;
    private Thing thing;

    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
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

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public LocalDate getDate() {
        return new LocalDate(date);
    }

    public void setDate(LocalDate date) {
        this.date = date.toString();
    }

    public void setId() {
        id = date.hashCode() + number + subject.hashCode();
    }
}
