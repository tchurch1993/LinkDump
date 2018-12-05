package com.linkdump.tchur.ld.objects;

import java.util.Calendar;

public class Task {
    private String Name;
    private Calendar dueDate;
    private Calendar startDate;

    @Override
    public String toString() {
        return "Task{" +
                "Name='" + Name + '\'' +
                ", dueDate=" + dueDate +
                ", startDate=" + startDate +
                ", Duration=" + Duration +
                '}';
    }

    private long Duration;

    public Task() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public long getDuration() {
        return Duration;
    }

    public void setDuration(long duration) {
        Duration = duration;
    }

    Task(String name, Calendar dueDate, Calendar startDate, long duration) {

        Name = name;
        this.dueDate = dueDate;
        this.startDate = startDate;
        Duration = duration;
    }
}
