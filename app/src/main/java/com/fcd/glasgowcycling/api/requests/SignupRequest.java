package com.fcd.glasgowcycling.api.requests;

import com.google.gson.annotations.Expose;

import retrofit.http.Query;

/**
 * Created by michaelhayes on 25/07/2014.
 */
public class SignupRequest {
    @Expose
    private UserSignup user;

    public SignupRequest(String email, String firstName, String lastName, String password,
                         String gender, String yearOfBirth, String profilePic){
        user = new UserSignup(email, firstName, lastName, password, gender, yearOfBirth, profilePic);
    }

    private class UserSignup {

        @Expose
        private String email;

        @Expose
        private String firstName;

        @Expose
        private String lastName;

        @Expose
        private String password;

        @Expose
        private String gender;

        @Expose
        private String yearOfBirth;

        @Expose
        private String profilePic;

        public UserSignup(String email, String firstName, String lastName, String password,
                             String gender, String yearOfBirth, String profilePic) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.password = password;
            this.gender = gender;
            this.yearOfBirth = yearOfBirth;
            this.profilePic = profilePic;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getYearOfBirth() {
            return yearOfBirth;
        }

        public void setYearOfBirth(String yearOfBirth) {
            this.yearOfBirth = yearOfBirth;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }
    }
}
