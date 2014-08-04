package com.fcd.glasgowcycling.api.http;

import com.fcd.glasgowcycling.api.responses.AuthModel;
import com.fcd.glasgowcycling.api.requests.SignupRequest;
import com.fcd.glasgowcycling.api.responses.RouteCaptureResponse;
import com.fcd.glasgowcycling.models.CaptureRoute;
import com.fcd.glasgowcycling.models.Route;
import com.fcd.glasgowcycling.models.RouteList;
import com.fcd.glasgowcycling.models.User;

import com.fcd.glasgowcycling.models.Weather;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
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
    void route(@Body CaptureRoute route, Callback<RouteCaptureResponse> callback);

    @POST("/signup.json")
    void signup(@Body SignupRequest body, Callback<AuthModel> callback);

    @GET("/routes/find/{id}.json")
    void routeDetails(@Path("id") int routeId, Callback<Route> routeDetails);

    @POST("/review.json")
    void reviewRoute(@Query("route_id") int routeId, @Query("rating") int rating, Callback<Route> route);

    @GET("/weather.json")
    void getWeather(Callback<Weather> weather);

    @PUT("/details.json")
    void updateDetails(@Query("first_name") String firstName, @Query("last_name") String lastName, @Query("profile_pic") String profilePic, @Query("email") String email, @Query("gender") String gender, Callback<User> callback);

    @POST("/reset_password.json")
    void resetPassword(@Query("old_password") String oldPassword, @Query("new_password") String newPassword, Callback<AuthModel> callback);

    @POST("forgotten_password.json")
    void forgottenPassword();
}
