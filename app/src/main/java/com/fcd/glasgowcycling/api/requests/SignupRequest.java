package com.fcd.glasgowcycling.api.requests;

import com.google.gson.annotations.Expose;

/**
 * Created by michaelhayes on 25/07/2014.
 */
public class SignupRequest {
    @Expose
    private UserSignup user;

    public SignupRequest(String email, String username, String password,
                         String gender, String yearOfBirth, String profilePic){
        user = new UserSignup(email, username, password, gender, yearOfBirth, profilePic);
    }

    private class UserSignup {

        @Expose
        private String email;

        @Expose
        private String username;

        @Expose
        private String password;

        @Expose
        private String gender;

        @Expose
        private String yearOfBirth;

        @Expose
        private String profilePic;

        public UserSignup(String email, String username, String password,
                             String gender, String yearOfBirth, String profilePic) {
            this.email = email;
            this.username = username;
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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
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
