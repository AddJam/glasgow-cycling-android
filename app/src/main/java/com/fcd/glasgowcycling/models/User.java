package com.fcd.glasgowcycling.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "User")
public class User{

    @DatabaseField(id = true)
    private String firstName;
    @DatabaseField
    private String lastName;
    @DatabaseField
    private String distanceStat;
    @DatabaseField
    private String timeStat;
    @DatabaseField
    private String email;
    @DatabaseField
    private String yearOfBirth;

    // Need this for ormlite
    public User() {

    }

    public User(String firstName, String lastName, String distanceStat, String timeStat, String email, String yearOfBirth){

        this.firstName = firstName;
        this.lastName = lastName;
        this.distanceStat = distanceStat;
        this.timeStat = timeStat;
        this.email = email;
        this.yearOfBirth = yearOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDistanceStat() {
        return distanceStat;
    }

    public void setDistanceStat(String distanceStat) {
        this.distanceStat = distanceStat;
    }

    public String getTimeStat() {
        return timeStat;
    }

    public void setTimeStat(String timeStat) {
        this.timeStat = timeStat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(String yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }
}