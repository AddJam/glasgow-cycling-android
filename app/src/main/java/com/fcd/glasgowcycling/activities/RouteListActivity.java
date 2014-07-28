package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.adapters.RouteAdapter;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.Route;
import com.fcd.glasgowcycling.models.RouteList;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RouteListActivity extends Activity {

    private final String TAG = "RouteList";

    private List<Route> routes;
    @InjectView(R.id.route_list) ListView routesList;
    @Inject GoCyclingApiInterface cyclingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list);
        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);

        routesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Route route = routes.get(position);
                if (route != null) {
                    Intent overviewIntent = new Intent(getBaseContext(), RouteOverviewActivity.class);
                    Bundle extras = new Bundle();
                    extras.putSerializable("route", route);
                    overviewIntent.putExtras(extras);
                    startActivity(overviewIntent);
                }
            }
        });

        if (routes != null) {
            routesList.setAdapter(new RouteAdapter(getBaseContext(), R.layout.route_cell, routes));
        } else {
            cyclingService.searchRoutes(true, new Callback<RouteList>() {
                @Override
                public void success(RouteList routeList, Response response) {
                    Log.d(TAG, "Got routes - total: " + routeList.getRoutes().size());
                    routes = routeList.getRoutes();
                    routesList.setAdapter(new RouteAdapter(getBaseContext(), R.layout.route_cell, routes));
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "Failed to get routes");
                }
            });
        }
    }

    private void refresh() {
        routesList.invalidateViews();
    }
}
