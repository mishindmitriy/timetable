package mishindmitriy.timetable.model;

import org.joda.time.DateTime;

import io.realm.RealmObject;

/**
 * Created by mishindmitriy on 04.11.2016.
 */
public class Message extends RealmObject {
    private String text;
    private long createdAt;

    public Message() {
        createdAt = DateTime.now().getMillis();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
