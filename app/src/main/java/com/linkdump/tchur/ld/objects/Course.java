package com.linkdump.tchur.ld.objects;

public class Course {
    String Name;

    public Course(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    @Override
    public String toString() {
        return "Course{" +
                "Name='" + Name + '\'' +
                '}';
    }

    public void setName(String name) {
        Name = name;
    }
}
