package com.fcd.glasgowcycling.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.LoadingView;
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

public class RouteListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private final String TAG = "RouteList";

    private List<Route> mRoutes;
    private Callback<RouteList> mSearchCallback;
    private ListView routesList;
    private LoadingView loadingView;
    @Inject GoCyclingApiInterface cyclingService;

    // Messages
    public String mEmptyMessage = "No routes found";
    public String mErrorMessage = "Error retrieving routes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list);
        ((CyclingApplication) getApplication()).inject(this);

        // Empty list view
        routesList = getListView();
        loadingView = (LoadingView) getListView().getEmptyView();
        loadingView.setBlue(true);

        // Search callback
        mSearchCallback = new Callback<RouteList>() {
            @Override
            public void success(RouteList routeList, Response response) {
                List<Route> retrievedRoutes = routeList.getRoutes();
                mRoutes = retrievedRoutes;
                Log.d(TAG, "Got routes - total: " + mRoutes.size());
                routesList.setAdapter(new RouteAdapter(getBaseContext(), R.layout.route_cell, mRoutes));
                if (mRoutes.size() == 0) {
                    searchFinished(mEmptyMessage);
                } else {
                    searchFinished(null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to get routes");
                searchFinished(mErrorMessage);
            }
        };

        // Route data - present if exists
        routesList.setOnItemClickListener(this);

        // Get data and perform search if possible
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("start_maidenhead") && bundle.containsKey("end_maidenhead")) {
            // Showing routes for a selected journey
            setTitle(bundle.getString("name"));
            int perPage = 1000;
            int pageNum = 1;
            loadingView.setRandomMessage();
            loadingView.startAnimating();
            cyclingService.searchRoutes(bundle.getString("start_maidenhead"), bundle.getString("end_maidenhead"), mSearchCallback);
        } else if (bundle.containsKey("user_only") || bundle.containsKey("source_lat") || bundle.containsKey("source_long")) {
            // Either user or nearby routes
            boolean userOnly = bundle.getBoolean("user_only");
            float sourceLat = bundle.getFloat("source_lat", 0.0f);
            float sourceLong = bundle.getFloat("source_long", 0.0f);
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
        loadingView.setRandomMessage();
        loadingView.startAnimating();
        if (userOnly) {
            cyclingService.routes(userOnly, mSearchCallback);
        } else {
            cyclingService.nearbyRoutes(sourceLat, sourceLong, mSearchCallback);
        }
    }

    private void searchFinished(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                loadingView.stopAnimating();
                if (message == null || message.isEmpty()) {
                    loadingView.hideMessage();
                } else {
                    loadingView.setMessage(message);
                }
            }
        });
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Route route = mRoutes.get(position);
        if (route == null) {
            return;
        }

        if (route.hasChildren()) {
            // Drill down into a journey
            Intent routeListIntent = new Intent(RouteListActivity.this, RouteListActivity.class);
            Bundle extras = new Bundle();
            extras.putSerializable("start_maidenhead", route.getStartMaidenhead());
            extras.putSerializable("end_maidenhead", route.getEndMaidenhead());
            extras.putSerializable("name", route.getStartName() + " to " + route.getEndName());
            routeListIntent.putExtras(extras);
            startActivity(routeListIntent);
        } else {
            // Show selected route
            Intent overviewIntent = new Intent(RouteListActivity.this, RouteOverviewActivity.class);
            Bundle extras = new Bundle();
            extras.putSerializable("route", route);
            overviewIntent.putExtras(extras);
            startActivity(overviewIntent);
        }
    }

}
