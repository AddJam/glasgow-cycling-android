package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.fcd.glasgowcycling.R;

import butterknife.InjectView;

public class RouteListActivity extends Activity {

    @InjectView(R.id.route_list) ListView routesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list);
    }
}
