package com.linkdump.tchur.ld.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private String courseName, description, eventName;
    private long startTime, endTime;
    private int startYear, startMonth, startDayOfMonth;
    private boolean overwrittable;

    public Event() {
    }

    protected Event(Parcel in) {
        courseName = in.readString();
        description = in.readString();
        eventName = in.readString();
        startTime = in.readLong();
        endTime = in.readLong();
        startYear = in.readInt();
        startMonth = in.readInt();
        startDayOfMonth = in.readInt();
        overwrittable = in.readByte() != 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getStartDayOfMonth() {
        return startDayOfMonth;
    }

    public void setStartDayOfMonth(int startDayOfMonth) {
        this.startDayOfMonth = startDayOfMonth;
    }

    public boolean isOverwrittable() {
        return overwrittable;
    }

    public void setOverwrittable(boolean overwrittable) {
        this.overwrittable = overwrittable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(courseName);
        dest.writeString(description);
        dest.writeString(eventName);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeInt(startYear);
        dest.writeInt(startMonth);
        dest.writeInt(startDayOfMonth);
        dest.writeByte((byte) (overwrittable ? 1 : 0));
    }
}
