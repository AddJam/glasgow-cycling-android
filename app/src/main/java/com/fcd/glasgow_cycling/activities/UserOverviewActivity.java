package com.fcd.glasgow_cycling.activities;

import com.fcd.glasgow_cycling.api.responses.RestError;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.crashlytics.android.Crashlytics;
import com.fcd.glasgow_cycling.CyclingApplication;
import com.fcd.glasgow_cycling.R;
import com.fcd.glasgow_cycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgow_cycling.api.responses.RouteCaptureResponse;
import com.fcd.glasgow_cycling.models.CaptureRoute;
import com.fcd.glasgow_cycling.models.Month;
import com.fcd.glasgow_cycling.models.User;
import com.fcd.glasgow_cycling.models.Weather;
import com.fcd.glasgow_cycling.utils.ActionBarFontUtil;
import com.fcd.glasgow_cycling.utils.AddJam;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.fabric.sdk.android.Fabric;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.ConversionException;

public class UserOverviewActivity extends Activity {
    @Inject
    GoCyclingApiInterface cyclingService;
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

    // Flash
    @InjectView(R.id.flash_message) View flashMessage;
    @InjectView(R.id.flash_text) TextView flashTextView;

    @InjectView(R.id.progress) SmoothProgressBar progressBar;

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

    private int numUploading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_overview);

        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);
        ActionBarFontUtil.setFont(this);

        // Stats
        statsButton.setOnClickListener(new StatsListener());
        captureButton.setOnClickListener(new CaptureListener());

        // Functions list view
        setupFunction(userRoutesView, R.drawable.my_routes_icon, "My Routes");
        setupFunction(nearbyRoutesView, R.drawable.nearby_routes_icon, "Nearby Routes");
        setupFunction(cycleMapView, R.drawable.cycle_map_icon, "Cycle Map");

        userRoutesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userRoutesIntent = new Intent(getBaseContext(), RouteListActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("user_only", true);
                extras.putString("title", "My Routes");
                userRoutesIntent.putExtras(extras);
                startActivity(userRoutesIntent);
            }
        });

        nearbyRoutesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userRoutesIntent = new Intent(getBaseContext(), RouteListActivity.class);
                Bundle extras = new Bundle();
                extras.putFloat("source_lat", (float) mUserLocation.latitude);
                extras.putFloat("source_long", (float) mUserLocation.longitude);
                extras.putString("title", "Nearby Routes");
                userRoutesIntent.putExtras(extras);
                startActivity(userRoutesIntent);
            }
        });

        cycleMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getBaseContext(), CycleMapActivity.class);
                startActivity(mapIntent);
            }
        });

        setupMap();

        Typeface regularFont = Typeface.createFromAsset(getAssets(), "fonts/FutureCityRegular.otf");
        Typeface semiBoldFont = Typeface.createFromAsset(getAssets(), "fonts/FutureCitySemiBold.otf");

        username.setTypeface(regularFont);
        captureButton.setTypeface(semiBoldFont);
        distanceStat.setTypeface(regularFont);
        timeStat.setTypeface(regularFont);
    }

    protected void onResume() {
        super.onResume();
        getDetails();
        getWeather();
        setupMap();
        updateRoutes();
    }

    private void updateRoutesPending() {
        List<CaptureRoute> routes = new Select().from(CaptureRoute.class).where("distance >= ?", 500).execute();

        if (routes.size() > 0) {
            // Show a message if we have any, until submitted
            String routeText = routes.size() == 1 ? "route" : "routes";
            String message = routes.size() + " pending " + routeText;
            showFlash(message);
        } else {
            hideFlash();
        }

        if (numUploading > 0) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateRoutes() {
        // Get all saved captured routes
        updateRoutesPending();
        List<CaptureRoute> routes = new Select().from(CaptureRoute.class).execute();
        numUploading = 0;

        if (routes.size() > 0) {
            // Upload them
            for (final CaptureRoute captureRoute : routes) {
                if (captureRoute.getDistance() < 500) {
                    // Too short!
                    captureRoute.delete();
                } else {
                    // Submit it
                    captureRoute.setPoints(captureRoute.points());
                    numUploading++;
                    updateRoutesPending();
                    cyclingService.route(captureRoute, new Callback<RouteCaptureResponse>() {
                        @Override
                        public void success(RouteCaptureResponse routeCaptureResponse, Response response) {
                            numUploading--;

                            // Now we can discard it
                            captureRoute.delete();
                            mUser.setUpdateRequired(true);
                            mUser.save();
                            updateRoutesPending();
                            AddJam.log(Log.INFO, TAG, "Submitted route successfully, id: " + routeCaptureResponse.getRouteId());
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            numUploading--;
                            AddJam.log(Log.INFO, TAG, "Failed to submit route");

                            if (error.getResponse() != null) {
                                try {
                                    RestError body = (RestError) error.getBodyAs(RestError.class);
                                    AddJam.log(Log.DEBUG, TAG, "Error " + error.getResponse().getStatus() + " submitting route:"  + body.error);

                                    if (error.getResponse().getStatus() == 422) {
                                        // Error was 422 unprocessable entity => problem with route
                                        Toast.makeText(getBaseContext(), "Error with route, failed to submit", Toast.LENGTH_LONG).show();
                                        captureRoute.delete();
                                    }
                                } catch (Exception e) {
                                    AddJam.log(Log.DEBUG, TAG, "Exception with route submit error");
                                }
                            }

                            updateRoutesPending();
                        }
                    });
                }
            }
        }
    }

    private void showFlash(String message) {
        Log.d(TAG, "Message is " + message);
        flashMessage.setVisibility(View.VISIBLE);
        flashTextView.setText(message);
    }

    private void hideFlash() {
        flashMessage.setVisibility(View.GONE);
    }

    private void setupMap() {
        // Check play services
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (isAvailable != ConnectionResult.SUCCESS) {
            noPlayServicesDialog();
            return;
        }

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
    }

    private void setupFunction(View view, int iconResource, String text) {
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        iconView.setImageResource(iconResource);

        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(text);

        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/FutureCitySemiBold.otf");
        textView.setTypeface(customFont);

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
        if (id == R.id.action_account_settings) {
            startActivity(new Intent(getApplicationContext(), AccountSettingsActivity.class));
            return true;
        }
        if (id == R.id.action_change_password) {
            startActivity(new Intent(getApplicationContext(), AccountPasswordActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getDetails(){
        // Check offline first
        User existingUser = ((CyclingApplication)getApplication()).getCurrentUser(false);
        boolean shouldUpdate = true;
        if (existingUser != null) {
            mUser = existingUser;
            populateFields();
            shouldUpdate = mUser.isUpdateRequired();
        }

        if (shouldUpdate) {
            AddJam.log(Log.INFO, TAG, "Updating user details");
            cyclingService.details(new Callback<User>() {

                @Override
                public void success(User user, Response response) {
                    AddJam.log(Log.INFO, TAG, "retrieved user details for " + user.getUserId());

                    // Delete existing users
                    new Delete().from(User.class).execute();

                    // Store
                    mUser = user;
                    mUser.setUpdateRequired(false);
                    mUser.getMonth().save();
                    mUser.save();

                    populateFields();
                }

                @Override
                public void failure(RetrofitError error) {
                    AddJam.log(Log.INFO, TAG, "Failed to get user details");
                }
            });
        } else {
            AddJam.log(Log.INFO, TAG, "Not updating user details");
        }
    }

    private void populateFields(){
        Month month = mUser.getMonth();

        username.setText(mUser.getUsername());
        distanceStat.setText(month.getReadableTime());
        timeStat.setText(month.getReadableDistance());
        if (mUser.getProfilePic() != null){
            String base64image = mUser.getProfilePic();
            byte[] decodedString = Base64.decode(base64image, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
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
            AddJam.log(Log.INFO, TAG, "Stats clicked");
            startActivity(new Intent(getApplicationContext(), UserStatsActivity.class));
        }
    }

    private class CaptureListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AddJam.log(Log.INFO, TAG, "CaptureRoute capture clicked");
            startActivity(new Intent(getApplicationContext(), RouteCaptureActivity.class));
        }
    }

    private void getWeather(){
        // Load offline
        Weather weather = new Select().from(Weather.class).limit(1).executeSingle();
        if (weather != null) {
            mWeather = weather;
            setWeather();
        }

        int currentTime = (int) (System.currentTimeMillis() / 1000L);
        int sinceLastHour = currentTime % 3600;
        int lastHourMark = currentTime - sinceLastHour;

        if (mWeather != null && mWeather.getTime() >= lastHourMark) {
            AddJam.log(Log.DEBUG, TAG, "Not updating weather, using cache");
            return;
        }

        // Load from API
        cyclingService.getWeather(new Callback<Weather>() {

            @Override
            public void success(Weather weather, Response response) {
                AddJam.log(Log.DEBUG, TAG, "retreived weather for period" + weather.getTime());
                // Delete existing weathers
                new Delete().from(Weather.class).execute();

                // Store
                mWeather = weather;
                mWeather.save();

                weatherArea.setVisibility(View.VISIBLE);
                setWeather();
            }

            @Override
            public void failure(RetrofitError error) {
                AddJam.log(Log.DEBUG, TAG, "Failed to get weather");
                weatherArea.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setWeather(){
        temperature.setText(mWeather.getReadableTemp());
        precipitation.setText(mWeather.getReadablePrecipitationProbability());
        windspeed.setText(mWeather.getReadableWindSpeed());
        weatherSource.setText(mWeather.getSource());

        String uri = "@drawable/"+mWeather.getUseableIcon();
        AddJam.log(Log.DEBUG, TAG, "Weather drawable " + uri);
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable icon = getResources().getDrawable(imageResource);

        weatherIcon.setImageDrawable(icon);

        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/FutureCitySemiBold.otf");
        temperature.setTypeface(customFont);
        precipitation.setTypeface(customFont);
        windspeed.setTypeface(customFont);
        weatherSource.setTypeface(customFont);
    }

    private void noPlayServicesDialog(){
        AddJam.log(Log.DEBUG, TAG, "No play services, showing dialog");

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Google Play Services Required");
        builder1.setMessage("Google Play Services from the Play store is required to record a route. Please download from the Play Store?");
        builder1.setCancelable(true);
        builder1.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder1.setPositiveButton("Download",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms&hl=en"));
                        startActivity(intent);
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
