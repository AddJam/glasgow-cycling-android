package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.models.Route;
import com.fcd.glasgowcycling.models.RoutePoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;

public class RouteCaptureActivity extends Activity {

    private static final String TAG = "RouteCaptureActivity";

    @InjectView(R.id.distance_info) TextView distanceInfo;
    @InjectView(R.id.time_info) TextView timeInfo;
    @InjectView(R.id.avg_speed_info) TextView avgSpeedInfo;
    @InjectView(R.id.speed_info) TextView speedInfo;
    @InjectView(R.id.finish_button) Button finishButton;

    private GoogleMap map;
    private LatLng userLocation;
    private LocationClient mLocationClient;

    private int timestamp;

    private Route route = new Route();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_capture);

        ButterKnife.inject(this);

        startLocationTracking();
        route.setStartTime(System.currentTimeMillis());

        // Show map
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setCompassEnabled(false);

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long millis = System.currentTimeMillis() - route.getStartTime();
                                timeInfo.setText(String.format("%02d:%02d.%02d",
                                        TimeUnit.MILLISECONDS.toHours(millis),
                                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                                ));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        finishButton.setOnClickListener(new FinishListener());

    }

    protected void startLocationTracking() {
       // if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
            mLocationClient = new LocationClient(this, mConnectionCallbacks, mConnectionFailedListener);
            mLocationClient.connect();
        //}
    }

    private GooglePlayServicesClient.ConnectionCallbacks mConnectionCallbacks = new GooglePlayServicesClient.ConnectionCallbacks() {

        @Override
        public void onDisconnected() {
        }

        @Override
        public void onConnected(Bundle arg0) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setFastestInterval(0);
            locationRequest.setInterval(0).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationClient.requestLocationUpdates(locationRequest, mLocationListener);
        }
    };

    private OnConnectionFailedListener mConnectionFailedListener = new OnConnectionFailedListener() {

        @Override
        public void onConnectionFailed(ConnectionResult arg0) {
            Log.e(TAG, "ConnectionFailed");
        }
    };

    private LocationListener mLocationListener = new LocationListener() {

        private long mLastEventTime = 0;

        @Override
        public void onLocationChanged(Location location) {
            double delayBtnEvents = (System.nanoTime()- mLastEventTime )/(1000000000.0);
            mLastEventTime = System.nanoTime();
            RoutePoint rp = new RoutePoint();

            //Sampling rate is the frequency at which updates are received
            String samplingRate = (new DecimalFormat("0.0000").format(1/delayBtnEvents));

            float speed = (float) (location.getSpeed() * 3.6);  // Converting m/s to Km/hr
            float lat = (float) (location.getLatitude());  // Converting m/s to Km/hr

            // add location to Route, route takes care of distance and avg speed
            route.addRoutePoint(location);

            speedInfo.setText(String.format("%.02f kph", speed));
            avgSpeedInfo.setText(String.format("%.02f kph", route.getAvgSpeed()));
            distanceInfo.setText(String.format("%.02f m", route.getDistance()));

            //use existing userlocation
            if (route.getPointsArray().size() > 1) {
                map.addPolyline(new PolylineOptions()
                        .add(userLocation, new LatLng(location.getLatitude(), location.getLongitude()))
                        .width(5)
                        .color(Color.BLUE));
            }

            // get the new location to keep the map moving
            userLocation = new LatLng(location.getLatitude(),location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));
        }
    };

    private class FinishListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Finish route capture clicked");
            if (route.getDistance() < 500){
                tooShortDialog();
            }
            else {
                startActivity(new Intent(getApplicationContext(), UserOverviewActivity.class));
                finish();
            }
        }
    }

    private void tooShortDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Route must be at least 500m in length. Stopping now it will not be recorded. Stop route capture?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(getApplicationContext(), UserOverviewActivity.class));
                        finish();
                    }
                });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
