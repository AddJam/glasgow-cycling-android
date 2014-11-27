package com.fcd.glasgowcycling.api.routes;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.adapters.RouteAdapter;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.Route;
import com.fcd.glasgowcycling.models.RouteList;

import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chrissloey on 26/11/2014.
 */
public abstract class RouteSearch {

    private Callback mSearchCallback;
    @Inject GoCyclingApiInterface cyclingService;

    public RouteSearch(GoCyclingApiInterface cyclingService) {
        this.cyclingService = cyclingService;
        mSearchCallback = new Callback<RouteList>() {
            @Override
            public void success(RouteList routeList, Response response) {
                onLoad(routeList.getRoutes());
            }

            @Override
            public void failure(RetrofitError error) {
                onFailure();
            }
        };
    }

    /*
     * Supported searches:
     *   - start_maidenhead to end_maidenhead
     *   - search with user_only=true
     *   - source lat and long
     *   - source lat and long to dest lat and long
     */
    public void search(Bundle query) {
        onStartLoad();
        int pageNum = query.getInt("page_num", 1);
        int perPage = query.getInt("per_page", 1000);
        if (query.containsKey("start_maidenhead") && query.containsKey("end_maidenhead")) {
            // Showing routes for a selected journey
            cyclingService.searchRoutes(perPage, pageNum,
                    query.getString("start_maidenhead"), query.getString("end_maidenhead"),
                    mSearchCallback);
        } else if (query.containsKey("user_only")) {
            boolean userOnly = query.getBoolean("user_only");
            if (userOnly) {
                cyclingService.routes(perPage, pageNum, userOnly, mSearchCallback);
            } else {
                // Invalid search query
                onFailure();
            }
        } else if (query.containsKey("dest_lat") && query.containsKey("dest_long")) {
            if (query.containsKey("source_lat") && query.containsKey("source_long")) {
                // Source an dest
                cyclingService.routesBetween(perPage, pageNum,
                        query.getFloat("source_lat", 0.0f), query.getFloat("source_long", 0.0f),
                        query.getFloat("dest_lat", 0.0f), query.getFloat("dest_long", 0.0f),
                        mSearchCallback);
            } else {
                // Only dest
                cyclingService.routesTo(perPage, pageNum,
                        query.getFloat("dest_lat", 0.0f), query.getFloat("dest_long", 0.0f),
                        mSearchCallback);
            }
        } else if (query.containsKey("source_lat") && query.containsKey("source_long")) {
            // Source with no dest == nearby
            cyclingService.nearbyRoutes(perPage, pageNum,
                    query.getFloat("source_lat", 0.0f), query.getFloat("source_long", 0.0f),
                        mSearchCallback);
        } else {
            // Couldn't do a search
            onFailure();
        }
    }

    public abstract void onStartLoad();

    public abstract void onLoad(List<Route> routes);

    public abstract void onFailure();
}
