package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.Month;
import com.fcd.glasgowcycling.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.MapFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private GoogleMap map;
    private LatLng userLocation;
    private LocationManager sLocationManager;

    @InjectView(R.id.functions_list) ListView functionsList;
    private List<Map<String,String>> functions = new ArrayList<Map<String,String>>();

    private User mUser;

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
            userLocation = new LatLng(55.8580, -4.259); // Glasgow
        }
        else {
            userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));

        sLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                Criteria.ACCURACY_COARSE, new JCLocationListener());

        // Load user details
        getDetails();

        // Stats
        statsButton.setOnClickListener(new StatsListener());

        // Functions list view
        initFunctionList();
    }

    private void initFunctionList() {
        functions.add(createFunction("function", "My Routes"));
        functions.add(createFunction("function", "Nearby Routes"));
        functions.add(createFunction("function", "Cycle Map"));

        SimpleAdapter simpleAdpt = new SimpleAdapter(this, functions, android.R.layout.simple_list_item_1, new String[] {"function"}, new int[] {android.R.id.text1});
        functionsList.setAdapter(simpleAdpt);
        functionsList.setOnItemClickListener(new TableListener());
    }

    private HashMap<String, String> createFunction(String key, String name) {
        HashMap<String, String> function = new HashMap<String, String>();
        function.put(key, name);

        return function;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_overview, menu);
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
        distanceStat.setText(String.valueOf(month.getKm()) + " km");
        timeStat.setText(String.valueOf(month.getSeconds()) + " seconds");
    }

    private class JCLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));
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
            //TODO Go to the Stats Activity
        }
    }

    private class TableListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
            TextView clickedView = (TextView) view;
            Log.d(TAG, "Item clicked" + id + "Position" + position);
        }
    }
}
