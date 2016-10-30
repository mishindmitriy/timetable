package mishindmitriy.timetable.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import mishindmitriy.timetable.utils.PairsTimeConverter;

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
    private boolean notified = false;

    public boolean isNotified() {
        return notified;
    }

    public void setNotified() {
        this.notified = true;
    }

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
        id = Math.abs(date.hashCode() + number + subject.hashCode());
    }

    public long getId() {
        return id;
    }

    private DateTime getDateTime(LocalDate localDate, String pairTime) {
        int hour, minutes;
        hour = Integer.valueOf(pairTime.substring(0, pairTime.indexOf(".")));
        minutes = Integer.valueOf(pairTime.substring(pairTime.indexOf(".") + 1, pairTime.length()));
        return localDate.toDateTimeAtStartOfDay(DateTimeZone.forOffsetHours(+4))
                .withTime(hour, minutes, 0, 0);
    }

    private boolean isSaturday() {
        return getDate().getDayOfWeek() == 6;
    }

    public DateTime getStartDateTime() {
        return getDateTime(getDate(), getStringStartTime());
    }

    public String getStringStartTime() {
        return PairsTimeConverter.getPairStartTime(getNumber(), isSaturday()).toString();
    }

    public DateTime getEndDateTime() {
        return getDateTime(getDate(), getStringEndTime());
    }

    public String getStringEndTime() {
        return PairsTimeConverter.getPairEndTime(getNumber(), isSaturday()).toString();
    }
}
