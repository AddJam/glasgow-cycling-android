package com.fcd.glasgow_cycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.fcd.glasgow_cycling.CyclingApplication;
import com.fcd.glasgow_cycling.R;
import com.fcd.glasgow_cycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgow_cycling.models.Point;
import com.fcd.glasgow_cycling.models.Route;
import com.fcd.glasgow_cycling.utils.ActionBarFontUtil;
import com.fcd.glasgow_cycling.utils.AddJam;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
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
    @InjectView(R.id.progress) SmoothProgressBar progressBar;

    @Inject GoCyclingApiInterface cyclingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_overview);
        ButterKnife.inject(this);
        ActionBarFontUtil.setFont(this);

        ((CyclingApplication) getApplication()).inject(this);

        // Present data
        Bundle bundle = getIntent().getExtras();
        Route route = (Route)bundle.getSerializable("route");
        AddJam.log(Log.INFO, TAG, "Overview for route " + route.getId() + " from " + route.getStartName());

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
        AddJam.log(Log.INFO, TAG, "Loading route details");
        progressBar.setVisibility(View.VISIBLE);
        cyclingService.routeDetails(route.getId(), new Callback<Route>() {
            @Override
            public void success(Route routeDetails, Response response) {
                AddJam.log(Log.INFO, TAG, "Successfully got route details");
                progressBar.setVisibility(View.GONE);
                List<Point> points = routeDetails.getPoints();
                if (points.size() > 1) {
                    map.clear();

                    // Move camera to start of route
                    LatLng routeStart = points.get(0).getLatLng();
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(routeStart, 15));

                    // Draw route line
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .width(10)
                            .color(getResources().getColor(R.color.jcBlueColor));
                    for (int i = 1; i < points.size(); i++) {
                        Point previousPoint = points.get(i - 1);
                        Point currentPoint = points.get(i);
                        polylineOptions.add(previousPoint.getLatLng(), currentPoint.getLatLng());
                    }
                    map.addPolyline(polylineOptions);

                    // Draw privacy circles
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
                AddJam.log(Log.INFO, TAG, "Failed to get route details");
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "Failed to load route", Toast.LENGTH_LONG).show();
            }
        });

        // Reviews
        final int routeId = route.getId();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                AddJam.log(Log.DEBUG, TAG, "Rating " + rating + " for route " + routeId);
                cyclingService.reviewRoute(routeId, (int)rating, new Callback<Route>() {
                    @Override
                    public void success(Route route, Response response) {
                        AddJam.log(Log.DEBUG, TAG, "Review successfully submitted");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        AddJam.log(Log.DEBUG, TAG, "Review failed to submit");
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
