package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.LoadingView;
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

public class RouteListActivity extends ListActivity {

    private final String TAG = "RouteList";

    private RouteClickListener mRouteClickListener;
    private List<Route> routes;
    private Callback<RouteList> mSearchCallback;
    private ListView routesList;
    private LoadingView loadingView;
    @Inject GoCyclingApiInterface cyclingService;

    // Messages
    public String mLoadingMessage = "Loading routes";
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
                Log.d(TAG, "Got routes - total: " + retrievedRoutes.size());
                mRouteClickListener.setRoutes(retrievedRoutes);
                routesList.setAdapter(new RouteAdapter(getBaseContext(), R.layout.route_cell, retrievedRoutes));
                if (retrievedRoutes.size() == 0) {
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
        int perPage = 1000;
        int pageNum = 1;
        loadingView.setMessage(mLoadingMessage);
        loadingView.startAnimating();
        if (userOnly) {
            cyclingService.routes(userOnly, perPage, pageNum, mSearchCallback);
        } else {
            cyclingService.searchRoutes(sourceLat, sourceLong, perPage, pageNum, mSearchCallback);
        }
    }

    private void searchFinished(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                loadingView.stopAnimating();
                if (message.isEmpty()) {
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

}
