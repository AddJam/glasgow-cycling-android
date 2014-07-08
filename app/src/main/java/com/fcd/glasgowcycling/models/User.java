package com.fcd.glasgowcycling.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.google.gson.annotations.Expose;

@DatabaseTable(tableName = "User")
public class User{

    @Expose @DatabaseField(id = true)
    private String userID;

    @Expose @DatabaseField
    private String firstName;

    @Expose @DatabaseField
    private String lastName;

    @Expose
    private Month month;

    @Expose @DatabaseField
    private String email;

    @Expose @DatabaseField
    private String gender;

    // Need this for ormlite
    public User() {

    }

    public User(String firstName, String lastName, Month month, String email, String gender){

        this.firstName = firstName;
        this.lastName = lastName;
        this.month = month;
        this.email = email;
        this.gender = gender;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getName() {
        return getFirstName() + " " + getLastName();
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}