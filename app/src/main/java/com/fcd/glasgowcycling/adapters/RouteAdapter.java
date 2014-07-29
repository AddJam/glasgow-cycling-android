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
        View view = convertView;
        ViewHolder holder; // to reference the child views for later actions

        if (view == null) {
            LayoutInflater viewInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = viewInflater.inflate(R.layout.route_cell, null);
            // cache view fields into the holder
            holder = new ViewHolder(view);
            // associate the holder with the view for later lookup
            view.setTag(holder);
        }
        else {
            // view already exists, get the holder instance from the view
            holder = (ViewHolder) view.getTag();
        }

        Route route = getItem(position);

        holder.toName.setText(route.getEndName());
        holder.fromName.setText(route.getStartName());
        holder.numReviews.setText("(" + route.getNumReviews() + ")");
        holder.averageDistance.setText(route.getAverages().getReadableDistance());
        holder.averageTime.setText(route.getAverages().getReadableTime());
        holder.rating.setRating(route.getAverages().getRating().floatValue());
        holder.numInstances.setText(route.getNumInstances() + " routes");

        return view;
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
