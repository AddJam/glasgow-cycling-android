package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.adapters.RouteClickListener;
import com.fcd.glasgowcycling.adapters.RouteAdapter;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.Route;
import com.fcd.glasgowcycling.models.RouteList;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RouteListActivity extends Activity {

    private final String TAG = "RouteList";

    private RouteClickListener mRouteClickListener;
    private List<Route> routes;
    private Callback<RouteList> mSearchCallback;
    @InjectView(R.id.route_list) ListView routesList;
    @Inject GoCyclingApiInterface cyclingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list);
        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);

        // Search callback
        mSearchCallback = new Callback<RouteList>() {
            @Override
            public void success(RouteList routeList, Response response) {
                Log.d(TAG, "Got routes - total: " + routeList.getRoutes().size());
                mRouteClickListener.setRoutes(routeList.getRoutes());
                routesList.setAdapter(new RouteAdapter(getBaseContext(), R.layout.route_cell, routeList.getRoutes()));                }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to get routes");
            }
        };

        // Route data - present if exists
        mRouteClickListener = new RouteClickListener(this);
        routesList.setOnItemClickListener(mRouteClickListener);
        if (routes != null) {
            mRouteClickListener.setRoutes(routes);
            routesList.setAdapter(new RouteAdapter(getBaseContext(), R.layout.route_cell, mRouteClickListener.getRoutes()));
        }

        // Get data and perform search if possible
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("user_only") || bundle.containsKey("source_lat") || bundle.containsKey("source_long")) {
            boolean userOnly = bundle.getBoolean("user_only");
            float sourceLat = bundle.getFloat("source_lat");
            float sourceLong = bundle.getFloat("source_long");
            search(userOnly, sourceLat, sourceLong);

            if (userOnly) {
                setTitle("My Routes");
            } else {
                setTitle("Nearby Routes");
            }
        } else {
            setTitle("Search");
        }
    }

    public void search(boolean userOnly, float sourceLat, float sourceLong) {
        int perPage = 1000;
        int pageNum = 1;
        if (userOnly) {
            cyclingService.routes(userOnly, perPage, pageNum, mSearchCallback);
        } else {
            cyclingService.searchRoutes(sourceLat, sourceLong, perPage, pageNum, mSearchCallback);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return (true);
        }
        return super.onOptionsItemSelected(item);
    }

}
