package com.fcd.glasgowcycling.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Users")
public class User extends Model {

    @Expose @Column(name = "UserId")
    private String userId;

    @Expose @Column(name = "FirstName")
    private String firstName;

    @Expose @Column(name = "LastName")
    private String lastName;

    @Expose @Column(name = "Month")
    private Month month;

    @Expose @Column(name = "Email")
    private String email;

    @Expose @Column(name = "Gender")
    private String gender;

    @Expose @Column(name = "ProfilePic")
    private String profilePic;

    // Need this for orm
    public User() {
        super();
    }

    public User(String firstName, String lastName, Month month, String email, String gender, String profilePic){
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.month = month;
        this.email = email;
        this.gender = gender;
        this.profilePic = profilePic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}