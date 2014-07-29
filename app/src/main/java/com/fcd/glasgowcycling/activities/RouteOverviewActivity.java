package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.Point;
import com.fcd.glasgowcycling.models.Route;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.CircleOptionsCreator;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RouteOverviewActivity extends Activity {

    private final String TAG = "Route Overview";

    @InjectView(R.id.rating) RatingBar ratingBar;
    @InjectView(R.id.time) TextView time;
    @InjectView(R.id.speed) TextView speed;
    @InjectView(R.id.distance) TextView distance;
    private GoogleMap map;

    @Inject GoCyclingApiInterface cyclingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_overview);
        ButterKnife.inject(this);
        ((CyclingApplication) getApplication()).inject(this);

        // Present data
        Bundle bundle = getIntent().getExtras();
        Route route = (Route)bundle.getSerializable("route");
        Log.d(TAG, "Route is from " + route.getStartName());

        ratingBar.setRating(route.getAverages().getRating().floatValue());
        time.setText(route.getAverages().getReadableTime());
        speed.setText(route.getAverages().getReadableSpeed());
        distance.setText(route.getAverages().getReadableDistance());

        // Setup map
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setCompassEnabled(true);

        // Load route points
        cyclingService.routeDetails(route.getId(), new Callback<Route>() {
            @Override
            public void success(Route routeDetails, Response response) {
                Log.d(TAG, "Successfully got route details");
                List<Point> points = routeDetails.getPoints();
                if (points.size() > 1) {
                    LatLng routeStart = points.get(0).getLatLng();
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(routeStart, 15));
                    for (int i = 1; i < points.size(); i++) {
                        Point previousPoint = points.get(i - 1);
                        Point currentPoint = points.get(i);
                        map.addPolyline(new PolylineOptions()
                                .add(previousPoint.getLatLng(), currentPoint.getLatLng())
                                .width(10)
                                .color(getResources().getColor(R.color.jcBlueColor)));
                    }
                    LatLng routeEnd = points.get(points.size() - 1).getLatLng();
                    map.addCircle(new CircleOptions()
                            .center(routeStart)
                            .radius(150)
                            .fillColor(getResources().getColor(R.color.jcTransparentBlueColor))
                            .strokeWidth(0));

                    map.addCircle(new CircleOptions()
                            .center(routeEnd)
                            .radius(150)
                            .fillColor(getResources().getColor(R.color.jcTransparentBlueColor))
                            .strokeWidth(0));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to get route details");
            }
        });

        // Reviews
        final int routeId = route.getId();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d(TAG, "Setting rating to " + rating);
                cyclingService.reviewRoute(routeId, (int)rating, new Callback<Route>() {
                    @Override
                    public void success(Route route, Response response) {
                        Log.d(TAG, "Review successfully submitted");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "Review failed to submit");
                    }
                });
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
