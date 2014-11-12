package com.fcd.glasgowcycling.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Users")
public class User extends Model {

    @Expose @Column(name = "UserId")
    private String userId;

    @Expose @Column(name = "Username")
    private String username;

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
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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