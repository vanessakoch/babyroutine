package com.example.baby_routine;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "event")
public class Event implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name = "image")
    private int image;
    @ColumnInfo(name = "action")
    private String action;
    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "hour")
    private String hour;

    public Event() {}

    public Event(int image, String action, String date, String hour) {
        this.image = image;
        this.action = action;
        this.date = date;
        this.hour = hour;
    }

    public Event(long id, int image, String action, String date, String hour) {
        this.id = id;
        this.image = image;
        this.action = action;
        this.date = date;
        this.hour = hour;
    }

    protected Event(Parcel in) {
        id = in.readLong();
        image = in.readInt();
        action = in.readString();
        date = in.readString();
        hour = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(image);
        dest.writeString(action);
        dest.writeString(date);
        dest.writeString(hour);
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {

        @Override
        public Event createFromParcel(Parcel in) {
            Event e = new Event();
            e.setId(in.readLong());
            e.setImage(in.readInt());
            e.setAction(in.readString());
            e.setDate(in.readString());
            e.setHour(in.readString());
            return e;
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return "\nEvent{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", date='" + date + '\'' +
                ", hour='" + hour + '\'' +
                '}';
    }
}
