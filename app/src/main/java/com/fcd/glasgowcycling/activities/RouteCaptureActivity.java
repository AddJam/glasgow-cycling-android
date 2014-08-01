package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.api.responses.RouteCaptureResponse;
import com.fcd.glasgowcycling.models.CaptureRoute;
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
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;

public class RouteCaptureActivity extends Activity {
    @Inject
    GoCyclingApiInterface cyclingService;
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

    private CaptureRoute captureRoute = new CaptureRoute();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_capture);
        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);

        mLocationClient = new LocationClient(this, mConnectionCallbacks, mConnectionFailedListener);
        mLocationClient.connect();
        captureRoute.setStartTime(System.currentTimeMillis());

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
                                long millis = System.currentTimeMillis() - captureRoute.getStartTime();
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

            //Sampling rate is the frequency at which updates are received
            String samplingRate = (new DecimalFormat("0.0000").format(1/delayBtnEvents));

            float speed = (float) (location.getSpeed());

            // add location to CaptureRoute, captureRoute takes care of distance and avg speed
            captureRoute.addRoutePoint(location);

            speedInfo.setText(String.format("%.02f kph", speed));
            avgSpeedInfo.setText(String.format("%.02f kph", captureRoute.getAvgSpeed()));
            distanceInfo.setText(String.format("%.02f m", captureRoute.getDistance()));

            //use existing userlocation
            if (captureRoute.getPoints().size() > 1) {
                map.addPolyline(new PolylineOptions()
                        .add(userLocation, new LatLng(location.getLatitude(), location.getLongitude()))
                        .width(10)
                        .color(getResources().getColor(R.color.jcBlueColor)));
            }

            // get the new location to keep the map moving
            userLocation = new LatLng(location.getLatitude(),location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    };

    private class FinishListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Finish route capture clicked");
            if (captureRoute.getDistance() < 500){
                tooShortDialog();
            }
            else {
                finishCapture(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (captureRoute.getDistance() < 500){
            tooShortDialog();
        }
        else {
            finishCapture(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    private void tooShortDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Route must be at least 500m");
        builder1.setMessage("If you stop now this route will not be recorded. Stop capturing this route?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishCapture(false);
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

    private void finishCapture(boolean submit){
        //if to submit Retrofit post
        if (submit) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
            String postJson = gson.toJson(captureRoute);

            cyclingService.route(captureRoute, new Callback<RouteCaptureResponse>() {
                @Override
                public void success(RouteCaptureResponse routeCaptureResponse, Response response) {
                    Log.d(TAG, "Submitted route successfully, id: " + routeCaptureResponse.getRouteId());
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "Failed to submit route");
                }
            });
        }
        finish();
    }
}
