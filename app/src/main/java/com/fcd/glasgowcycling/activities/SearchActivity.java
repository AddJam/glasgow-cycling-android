package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.adapters.RouteAdapter;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.RouteList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchActivity extends Activity {

    @InjectView(R.id.route_list) ListView routesList;
    @Inject GoCyclingApiInterface cyclingService;

    private final String TAG = "Search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list);
        ButterKnife.inject(this);
        ((CyclingApplication) getApplication()).inject(this);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            cyclingService.searchRoutes(true, new Callback<RouteList>() {
                @Override
                public void success(RouteList routeList, Response response) {
                    Log.d(TAG, "Got routes - total: " + routeList.getRoutes().size());
                    routesList.setAdapter(new RouteAdapter(getBaseContext(), R.layout.route_cell, routeList.getRoutes()));
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "Failed to get routes");
                }
            });
        }
    }
}
