package mishindmitriy.timetable.model.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mishindmitriy on 05.06.2015.
 */
@DatabaseTable
public class Pair implements Serializable {

    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField(dataType = DataType.STRING)
    private String classroom;
    @DatabaseField(dataType = DataType.BYTE_OBJ)
    private Byte number;
    @DatabaseField(dataType = DataType.STRING)
    private String teacher;
    @DatabaseField(dataType = DataType.STRING)
    private String type;
    @DatabaseField(dataType = DataType.STRING)
    private String subject;
    @DatabaseField(dataType = DataType.STRING)
    private String groups;
    @DatabaseField(dataType = DataType.DATE_LONG, index = true)
    private Date date;
    @DatabaseField(dataType = DataType.STRING)
    private String note;

    public Pair() {

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
