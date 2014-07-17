package com.fcd.glasgowcycling.api.http;

import com.fcd.glasgowcycling.api.AuthModel;
import com.fcd.glasgowcycling.models.CapturePoints;
import com.fcd.glasgowcycling.models.CaptureRoute;
import com.fcd.glasgowcycling.models.User;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by chrissloey on 01/07/2014.
 */
public interface GoCyclingApiInterface {
    @POST("/oauth/token?grant_type=password")
    AuthModel signin(@Query("email") String email, @Query("password") String password);

    @POST("/oauth/token?grant_type=refresh_token")
    AuthModel refreshToken(@Query("refresh_token") String refreshToken);

    @GET("/details.json")
    void details(Callback<User> callback);

    @POST("/routes.json")
    void route(@Body ArrayList<CapturePoints> points);
}
