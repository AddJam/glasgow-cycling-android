package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.adapters.RouteAdapter;
import com.fcd.glasgowcycling.models.Route;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RouteListActivity extends Activity {

    @InjectView(R.id.route_list) ListView routesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list);
        ButterKnife.inject(this);
        List<Route> routes = new ArrayList<Route>();
        routes.add(getRoute());
        routes.add(getRoute());
        routes.add(getRoute());
        routesList.setAdapter(new RouteAdapter(this, R.layout.route_cell, routes));
    }

    public Route getRoute() {
        Route r = new Route();
        r.setName("pew");
        return r;
    }
}
