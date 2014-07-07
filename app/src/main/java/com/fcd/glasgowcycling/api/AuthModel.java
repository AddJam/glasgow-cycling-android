package com.fcd.glasgowcycling.api;

import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.google.gson.annotations.Expose;

import javax.inject.Inject;

/**
 * Created by chrissloey on 01/07/2014.
 */
public class AuthModel {
    @Expose
    private String accessToken;

    @Expose
    private String refreshToken;

    public String getUserToken() {
        return accessToken;
    }

    public void setUserToken(String userToken) {
        this.accessToken = userToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
