package com.linkdump.tchur.ld.objects;


import java.util.Calendar;

public class Assignment extends Task {

    Course subject;

    public Assignment(Course subject) {
        this.subject = subject;
    }

    public Assignment(String name, Calendar dueDate, Calendar startDate, long duration) {
        super(name, dueDate, startDate, duration);
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "subject=" + subject +
                '}';
    }

    public Course getSubject() {
        return subject;
    }

    public void setSubject(Course subject) {
        this.subject = subject;
    }
}
