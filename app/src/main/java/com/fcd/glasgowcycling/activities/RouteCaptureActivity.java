package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
                                timeInfo.setText(String.format("%d:%d.%d",
                                        TimeUnit.MILLISECONDS.toHours(millis),
                                        TimeUnit.MILLISECONDS.toMinutes(millis),
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.route_capture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

//              TODO fix types
//            rp.setAltitude(location.getAltitude());
            rp.setCourse(location.getBearing());
            rp.setLat(location.getLatitude());
            rp.setLng(location.getLongitude());
//            rp.setTime(System.nanoTime());
            rp.sethAccuracy(location.getAccuracy());
            rp.sethAccuracy(location.getAccuracy());
            rp.setSpeed(location.getSpeed());

            route.getPointsArray().add(rp);



            speedInfo.setText(speed + " kmph"); //Updating UI
//             TODO this
//            avgSpeedInfo.setText(route.getAvgSpeed());
//            distanceInfo.setText(route.getDistance());
            Log.e(TAG, lat + " lat"); //Updating UI

            userLocation = new LatLng(location.getLatitude(),location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));
        }
    };
}
