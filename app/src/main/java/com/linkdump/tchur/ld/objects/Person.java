package com.linkdump.tchur.ld.objects;


public class Person {

    String Name, LastName;

    public Person(String name, String lastName) {
        Name = name;
        LastName = lastName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "Name='" + Name + '\'' +
                ", LastName='" + LastName + '\'' +
                '}';
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }
}
