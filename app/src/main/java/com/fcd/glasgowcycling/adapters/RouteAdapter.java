package com.fcd.glasgowcycling.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.models.Route;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chrissloey on 16/07/2014.
 */
public class RouteAdapter extends ArrayAdapter<Route> {

    private final String TAG = "RouteAdapter";

    public RouteAdapter(Context context, int resourceId, List<Route> routes) {
        super(context, resourceId, routes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Getting view for position " + position);
        View v = convertView;
        ViewHolder holder; // to reference the child views for later actions

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.route_cell, null);
            // cache view fields into the holder
            holder = new ViewHolder(v);
            // associate the holder with the view for later lookup
            v.setTag(holder);
        }
        else {
            // view already exists, get the holder instance from the view
            holder = (ViewHolder) v.getTag();
        }

        holder.toName.setText("Pew");

        return v;
    }

    // somewhere else in your class definition
    static class ViewHolder {
        @InjectView(R.id.toName) TextView toName;
        @InjectView(R.id.fromName) TextView fromName;
        @InjectView(R.id.rating) RatingBar rating;
        @InjectView(R.id.numReviews) TextView numReviews;
        @InjectView(R.id.averageDistance) TextView averageDistance;
        @InjectView(R.id.averageTime) TextView averageTime;
        @InjectView(R.id.numInstances) TextView numInstances;
        @InjectView(R.id.typeImage) ImageView typeImage;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
