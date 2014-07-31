package com.fcd.glasgowcycling.api.http;

import com.fcd.glasgowcycling.api.AuthModel;
import com.fcd.glasgowcycling.api.SignupRequest;
import com.fcd.glasgowcycling.models.Route;
import com.fcd.glasgowcycling.models.RouteList;
import com.fcd.glasgowcycling.models.User;

import java.util.List;
import com.fcd.glasgowcycling.models.CapturePoints;

import java.util.ArrayList;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
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

    @GET("/routes.json")
    void routes(@Query("user_only") boolean userOnly, @Query("per_page") int perPage, @Query("page_num") int pageNum, Callback<RouteList> callback);

    @GET("/routes.json")
    void searchRoutes(@Query("source_lat") float sourceLat, @Query("source_long") float sourceLong,
                    @Query("per_page") int perPage, @Query("page_num") int pageNum, Callback<RouteList> callback);

    @POST("/routes.json")
    void route(@Body ArrayList<CapturePoints> points);

    @POST("/signup.json")
    void signup(@Body SignupRequest body, Callback<AuthModel> callback);

    @GET("/routes/find/{id}.json")
    void routeDetails(@Path("id") int routeId, Callback<Route> routeDetails);

    @POST("/review.json")
    void reviewRoute(@Query("route_id") int routeId, @Query("rating") int rating, Callback<Route> route);
}
