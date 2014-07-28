package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.models.Route;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RouteOverviewActivity extends Activity {

    private final String TAG = "Route Overview";

    @InjectView(R.id.rating) RatingBar ratingBar;
    @InjectView(R.id.time) TextView time;
    @InjectView(R.id.speed) TextView speed;
    @InjectView(R.id.distance) TextView distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_overview);
        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        Route route = (Route)bundle.getSerializable("route");
        Log.d(TAG, "Route is from " + route.getStartName());

        ratingBar.setRating(route.getAverages().getRating().floatValue());
        time.setText(route.getAverages().getReadableTime());
        speed.setText(route.getAverages().getReadableSpeed());
        distance.setText(route.getAverages().getReadableDistance());
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
