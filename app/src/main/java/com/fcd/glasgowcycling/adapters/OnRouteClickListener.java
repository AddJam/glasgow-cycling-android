package com.fcd.glasgowcycling.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.fcd.glasgowcycling.activities.RouteOverviewActivity;
import com.fcd.glasgowcycling.models.Route;

import java.util.List;

/**
 * Created by chrissloey on 29/07/2014.
 */
public class OnRouteClickListener implements AdapterView.OnItemClickListener {
    private Context mContext;
    private List<Route> mRoutes;

    public OnRouteClickListener(Context context) {
        mContext = context;
    }

    public void setRoutes(List<Route> routes) {
        mRoutes = routes;
    }

    public List<Route> getRoutes() {
        return mRoutes;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Route route = mRoutes.get(position);
        if (route != null) {
            Intent overviewIntent = new Intent(mContext, RouteOverviewActivity.class);
            Bundle extras = new Bundle();
            extras.putSerializable("route", route);
            overviewIntent.putExtras(extras);
            mContext.startActivity(overviewIntent);
        }
    }
}
