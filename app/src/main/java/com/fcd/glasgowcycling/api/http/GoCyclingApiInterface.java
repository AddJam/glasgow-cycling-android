package com.fcd.glasgowcycling.api.http;

import com.fcd.glasgowcycling.api.AuthModel;

import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by chrissloey on 01/07/2014.
 */
public interface GoCyclingApiInterface {
    @POST("/oauth/token?grant_type=password")
    AuthModel signin(@Query("email") String email, @Query("password") String password);

    @POST("/oauth/token?grant_type=refresh_token")
    AuthModel refreshToken();
}
