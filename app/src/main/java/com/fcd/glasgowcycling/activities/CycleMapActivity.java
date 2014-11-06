package com.fcd.glasgowcycling.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.Poi;
import com.fcd.glasgowcycling.models.PoiList;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CycleMapActivity extends FragmentActivity {

    @Inject
    GoCyclingApiInterface mCyclingService;

    private GoogleMap mMap;
    private PoiList mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CyclingApplication) getApplication()).inject(this);

        setContentView(R.layout.activity_cycle_map);
        setUpMapIfNeeded();

        mList = new Select().from(PoiList.class).limit(1).executeSingle();

        if (mList == null) {
            mCyclingService.pointsOfInterest(new Callback<PoiList>() {
                @Override
                public void success(PoiList poiList, Response response) {
                    mList = poiList;
                    showLocations();
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getBaseContext(), "Network error retrieving locations",
                            Toast.LENGTH_LONG).show();
                }
            });
        } else {
            showLocations();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    public void showLocations() {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.clear();
                for (Poi poi : mList.getLocations()) {
                    LatLng location = new LatLng(poi.getLat(), poi.getLng());
                    MarkerOptions options = new MarkerOptions()
                            .position(location);
                    if (poi.getName() != null && poi.getName().length() > 0) {
                        options.title(poi.getName());
                    } else {
                        String capitalizedType = poi.getType().toUpperCase().charAt(0) + poi.getType().substring(1);
                        options.title(capitalizedType);
                    }
                    mMap.addMarker(options);
                }
            }
        });
    }
}
