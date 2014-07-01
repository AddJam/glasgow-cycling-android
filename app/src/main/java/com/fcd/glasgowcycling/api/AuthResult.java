package com.fcd.glasgowcycling.api;

import com.google.gson.annotations.Expose;

/**
 * Created by chrissloey on 01/07/2014.
 */
public class AuthResult {
    @Expose
    private String userToken;

    public String getUserToken() {
        return userToken;
    }

    public void setUser_token(String userToken) {
        this.userToken = userToken;
    }

}
