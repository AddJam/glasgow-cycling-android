package com.fcd.glasgow_cycling.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fcd.glasgow_cycling.R;
import com.fcd.glasgow_cycling.models.Route;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chrissloey on 16/07/2014.
 */
public class RouteAdapter extends ArrayAdapter<Route> {

    private final String TAG = "RouteAdapter";
    private boolean mLoading;

    public RouteAdapter(Context context, int resourceId, List<Route> routes) {
        super(context, resourceId, routes);
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mLoading) {
            return super.getCount() + 1;
        } else {
            return super.getCount();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == super.getCount() && mLoading) {
            LayoutInflater viewInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return viewInflater.inflate(R.layout.loading_cell, null);
        }

        View view = convertView;
        ViewHolder holder; // to reference the child views for later actions

        if (view == null || view.getTag() == null) {
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

        if (route.getEndName() != null) {
            holder.toRow.setVisibility(View.VISIBLE);
            holder.toName.setText(route.getEndName());
        } else {
            holder.toRow.setVisibility(View.GONE);
        }
        if (route.getStartName() != null) {
            holder.fromRow.setVisibility(View.VISIBLE);
            holder.fromName.setText(route.getStartName());
        } else {
            holder.fromRow.setVisibility(View.GONE);
        }
        holder.numReviews.setText("(" + route.getNumReviews() + ")");
        holder.averageDistance.setText(route.getAverages().getReadableDistance());
        holder.averageTime.setText(route.getAverages().getReadableTime());
        holder.rating.setRating(route.getAverages().getRating().floatValue());
        holder.numInstances.setText(route.getNumInstances() + " routes");

        Typeface semiBoldFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/FutureCitySemiBold.otf");
        Typeface regularFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/FutureCityRegular.otf");
        holder.fromName.setTypeface(semiBoldFont);
        holder.toName.setTypeface(semiBoldFont);
        holder.toLabel.setTypeface(regularFont);
        holder.fromLabel.setTypeface(regularFont);

        return view;
    }

    // somewhere else in your class definition
    static class ViewHolder {
        @InjectView(R.id.toText) RelativeLayout toRow;
        @InjectView(R.id.fromText) RelativeLayout fromRow;
        @InjectView(R.id.toName) TextView toName;
        @InjectView(R.id.toLabel) TextView toLabel;
        @InjectView(R.id.fromName) TextView fromName;
        @InjectView(R.id.fromLabel) TextView fromLabel;
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
