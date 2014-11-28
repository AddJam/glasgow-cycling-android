package com.fcd.glasgowcycling.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.fcd.glasgowcycling.api.routes.RouteSearch;
import com.fcd.glasgowcycling.models.Route;
import com.fcd.glasgowcycling.models.RouteList;
import com.fcd.glasgowcycling.utils.ActionBarFontUtil;
import com.fcd.glasgowcycling.utils.EndlessScrollListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RouteListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private final String TAG = "RouteList";

    static final int PER_PAGE = 10;
    int mCurrentPage = 1;
    boolean mReachedBottom;
    Bundle mQuery;

    private List<Route> mRoutes;
    private ListView routesList;
    private LoadingView loadingView;
    private View mFooterView;

    RouteSearch mSearcher;
    @Inject GoCyclingApiInterface cyclingService;

    // Messages
    public String mEmptyMessage = "No routes found";
    public String mErrorMessage = "Error retrieving routes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list);
        ActionBarFontUtil.setFont(this);

        ((CyclingApplication) getApplication()).inject(this);
        mRoutes = new ArrayList<Route>();
        mReachedBottom = false;

        // Empty list view
        routesList = getListView();
        routesList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TAG, "Loading page " + page + " total items is " + totalItemsCount);
                if (!mReachedBottom) {
                    mCurrentPage = page;
                    performSearch();
                }
            }
        });
        setListAdapter(new RouteAdapter(getBaseContext(), R.layout.route_cell, mRoutes));

        // Loading view for footer (infinite scrolling)
        LayoutInflater viewInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFooterView = viewInflater.inflate(R.layout.loading_cell, null);

        // Loading view for empty list
        loadingView = (LoadingView) getListView().getEmptyView();
        loadingView.setBlue(true);

        // Search callback
        mSearcher = new RouteSearch(cyclingService) {
            @Override
            public void onStartLoad() {
                if (mRoutes.size() == 0) {
                    loadingView.setRandomMessage();
                    loadingView.startAnimating();
                } else {
                    // Show loading indicator
                    setLoading(true);
                }
            }

            @Override
            public void onLoad(List<Route> routes) {
                Log.d(TAG, "Got " + routes.size() + " more routes - total: " + mRoutes.size());

                if (routes.size() == 0) {
                    mReachedBottom = true;
                } else {
                    // Add routes to list
                    RouteAdapter adapter = ((RouteAdapter) getListAdapter());
                    for (Route route : routes) {
                        adapter.add(route); // This also adds to mRoutes
                    }
                }

                // Finished loading
                if (mRoutes.size() == 0) {
                    searchFinished(mEmptyMessage);
                } else {
                    searchFinished(null);
                }
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "Failed to get routes");
                searchFinished(mErrorMessage);
            }
        };

        // Route data - present if exists
        routesList.setOnItemClickListener(this);

        // Get data and perform search if possible
        Bundle bundle = getIntent().getExtras();

        if (bundle.containsKey("title")) {
            setTitle(bundle.getString("title"));
        }
        setQuery(bundle);
        performSearch();
    }

    public void setQuery(Bundle query) {
        mQuery = query;
    }

    public void setLoading(boolean loading) {
        ((RouteAdapter)getListAdapter()).setLoading(loading);
    }

    public void performSearch() {
        if (mQuery != null) {
            mQuery.putInt("per_page", PER_PAGE);
            mQuery.putInt("page_num", mCurrentPage);
            mSearcher.search(mQuery);
        }
    }

    private void searchFinished(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                setLoading(false);
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
            extras.putSerializable("title", route.getStartName() + " to " + route.getEndName());
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

    @Override
    public void onBackPressed() {
        getListView().setOnScrollListener(null);
        super.onBackPressed();
    }
}
