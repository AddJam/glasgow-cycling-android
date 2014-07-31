package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
<<<<<<< HEAD
import android.widget.SearchView;
=======
import android.widget.LinearLayout;
>>>>>>> ca82fb72436a8ce012fb06772d8fdd1e3918c4cc
import android.widget.TextView;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.Month;
import com.fcd.glasgowcycling.models.User;
import com.fcd.glasgowcycling.models.Weather;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserOverviewActivity extends Activity {
    @Inject GoCyclingApiInterface cyclingService;
    private static final String TAG = "OverviewActivity";

    @InjectView(R.id.username) TextView username;
    @InjectView(R.id.distance_stat) TextView distanceStat;
    @InjectView(R.id.time_stat) TextView timeStat;
    @InjectView(R.id.user_stats_button) Button statsButton;
    @InjectView(R.id.profile_image) ImageView profileImage;
    @InjectView(R.id.capture_button) Button captureButton;

    @InjectView(R.id.user_routes) View userRoutesView;
    @InjectView(R.id.nearby_routes) View nearbyRoutesView;
    @InjectView(R.id.cycle_map) View cycleMapView;

    //weather
    @InjectView(R.id.temp_info) TextView temperature;
    @InjectView(R.id.precip_info) TextView precipitation;
    @InjectView(R.id.wind_speed_info) TextView windspeed;
    @InjectView(R.id.weather_source) TextView weatherSource;
    @InjectView(R.id.weather_icon) ImageView weatherIcon;
    @InjectView(R.id.weather_area) LinearLayout weatherArea;

    private GoogleMap map;
    private LatLng mUserLocation;
    private LocationManager sLocationManager;

    private User mUser;
    private Weather mWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_overview);

        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);

        // Show map
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);
        map.setMyLocationEnabled(true);

        sLocationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = sLocationManager.getBestProvider(criteria, false);
        Location location = sLocationManager.getLastKnownLocation(provider);
        if(location == null){
            mUserLocation = new LatLng(55.8580, -4.259); // Glasgow
        } else {
            mUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mUserLocation, 13));

        sLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                Criteria.ACCURACY_COARSE, new JCLocationListener());

        // Load user details
        getDetails();

        //Get and set weather
        getWeather();

        // Stats
        statsButton.setOnClickListener(new StatsListener());
        captureButton.setOnClickListener(new CaptureListener());

        // Functions list view
        setupFunction(userRoutesView, R.drawable.logo, "My Routes");
        setupFunction(nearbyRoutesView, R.drawable.logo, "Nearby Routes");
        setupFunction(cycleMapView, R.drawable.logo, "Cycle Map");

        userRoutesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userRoutesIntent = new Intent(getBaseContext(), RouteListActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("user_only", true);
                userRoutesIntent.putExtras(extras);
                startActivity(userRoutesIntent);
            }
        });

        nearbyRoutesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userRoutesIntent = new Intent(getBaseContext(), RouteListActivity.class);
                Bundle extras = new Bundle();
                extras.putDouble("source_lat", mUserLocation.latitude);
                extras.putDouble("source_long", mUserLocation.longitude);
                userRoutesIntent.putExtras(extras);
                startActivity(userRoutesIntent);
            }
        });
    }

    private void setupFunction(View view, int iconResource, String text) {
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        iconView.setImageResource(iconResource);

        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_overview, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(this.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

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

    private void getDetails(){
        cyclingService.details(new Callback<User>() {

            @Override
            public void success(User user, Response response) {
                Log.d(TAG, "retreived user details for " + user.getUserId());
                mUser = user;
                populateFields();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to get user details");
            }
        });
    }

    private void populateFields(){
        Month month = mUser.getMonth();

        username.setText(mUser.getName());
        distanceStat.setText(month.getReadableTime());
        timeStat.setText(month.getReadableDistance());
        Bitmap decodedImage;
        if (mUser.getProfilePic() != null){
            byte[] decodedString = Base64.decode(mUser.getProfilePic(), Base64.DEFAULT);
            decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(decodedImage);
        }
    }

    private class JCLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            mUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mUserLocation, 13));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    private class StatsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Stats clicked");
            //TODO implement stats

        }
    }

    private class CaptureListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "CaptureRoute capture clicked");
            startActivity(new Intent(getApplicationContext(), RouteCaptureActivity.class));
        }
    }

    private void getWeather(){
        cyclingService.getWeather(new Callback<Weather>() {

            @Override
            public void success(Weather weather, Response response) {
                Log.d(TAG, "retreived weather for period" + weather.getTime());
                mWeather = weather;
                weatherArea.setVisibility(View.VISIBLE);
                setWeather();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to get weather");
                weatherArea.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setWeather(){
        temperature.setText(mWeather.getReadableTemp());
        precipitation.setText(mWeather.getReadablePrecipitationProbability());
        windspeed.setText(mWeather.getReadableWindSpeed());
        weatherSource.setText(mWeather.getSource());

        String uri = "@drawable/"+mWeather.getIcon();
        Log.d(TAG, "Weather drawable " + uri);
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable icon = getResources().getDrawable(imageResource);

        weatherIcon.setImageDrawable(icon);
    }

}
