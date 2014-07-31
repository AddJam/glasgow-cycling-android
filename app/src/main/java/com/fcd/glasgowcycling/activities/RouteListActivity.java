package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.adapters.RouteClickListener;
import com.fcd.glasgowcycling.adapters.RouteAdapter;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.RouteList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RouteListActivity extends Activity {

    private final String TAG = "RouteList";

    private RouteClickListener mRouteClickListener;
    @InjectView(R.id.route_list) ListView routesList;
    @Inject GoCyclingApiInterface cyclingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list);
        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);

        if (mRouteClickListener != null) {
            routesList.setOnItemClickListener(mRouteClickListener);
            routesList.setAdapter(new RouteAdapter(getBaseContext(), R.layout.route_cell, mRouteClickListener.getRoutes()));
        } else {
            mRouteClickListener = new RouteClickListener(this);
            routesList.setOnItemClickListener(mRouteClickListener);

            int perPage = 1000;
            int pageNum = 1;
            cyclingService.routes(true, perPage, pageNum, new Callback<RouteList>() {
                @Override
                public void success(RouteList routeList, Response response) {
                    Log.d(TAG, "Got routes - total: " + routeList.getRoutes().size());
                    mRouteClickListener.setRoutes(routeList.getRoutes());
                    routesList.setAdapter(new RouteAdapter(getBaseContext(), R.layout.route_cell, routeList.getRoutes()));                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "Failed to get routes");
                }
            });
        }
    }

}
