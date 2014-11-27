package com.fcd.glasgowcycling.api.http;

import com.fcd.glasgowcycling.api.requests.SignupRequest;
import com.fcd.glasgowcycling.api.responses.AuthModel;
import com.fcd.glasgowcycling.api.responses.RouteCaptureResponse;
import com.fcd.glasgowcycling.models.CaptureRoute;
import com.fcd.glasgowcycling.models.PoiList;
import com.fcd.glasgowcycling.models.Overall;
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
    void routes(@Query("per_page") int perPage, @Query("page_num") int pageNum,
                @Query("user_only") boolean userOnly, Callback<RouteList> callback);

    @GET("/routes.json")
    void searchRoutes(@Query("per_page") int perPage, @Query("page_num") int pageNum,
                      @Query("start_maidenhead") String startMaidenhead, @Query("end_maidenhead") String endMaidenhead,
                      Callback<RouteList> callback);

    @GET("/routes.json")
    void nearbyRoutes(@Query("per_page") int perPage, @Query("page_num") int pageNum,
                      @Query("source_lat") float sourceLat, @Query("source_long") float sourceLong,
                      Callback<RouteList> callback);

    @GET("/routes.json")
    void routesTo(@Query("per_page") int perPage, @Query("page_num") int pageNum,
                  @Query("dest_lat") float destLat, @Query("dest_long") float destLong,
                      Callback<RouteList> callback);

    @GET("/routes.json")
    void routesBetween(@Query("per_page") int perPage, @Query("page_num") int pageNum,
                       @Query("source_lat") float sourceLat, @Query("source_long") float sourceLong,
                       @Query("dest_lat") float destLat, @Query("dest_long") float destLong,
                       Callback<RouteList> callback);

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
    void updateDetails(@Query("username") String username, @Query("profile_pic") String profilePic, @Query("email") String email, @Query("gender") String gender, Callback<User> callback);

    @POST("/reset_password.json")
    void resetPassword(@Query("old_password") String oldPassword, @Query("new_password") String newPassword, Callback<AuthModel> callback);

    @POST("/forgot_password.json")
    void forgottenPassword(@Query("email") String email, Callback<User> user);

    @GET("/poi/all.json")
    void pointsOfInterest(Callback<PoiList> points);

    @GET("/stats/days.json")
    void getStats(@Query("num_days") int numDays, Callback<Overall> overall);
}
