package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.models.Route;

public class RouteOverviewActivity extends Activity {

    private final String TAG = "Route Overview";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_overview);
        Bundle bundle = getIntent().getExtras();
        Route route = (Route)bundle.getSerializable("route");
        Log.d(TAG, "Route is from " + route.getStartName());
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
