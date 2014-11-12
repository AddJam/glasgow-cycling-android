package com.fcd.glasgowcycling.activities;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.androidmapsextensions.ClusterOptions;
import com.androidmapsextensions.ClusterOptionsProvider;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.SupportMapFragment;
import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.Poi;
import com.fcd.glasgowcycling.models.PoiList;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CycleMapActivity extends FragmentActivity {

    @Inject
    GoCyclingApiInterface mCyclingService;

    private GoogleMap mMap;
    private PoiList mList;
    private List<String> mTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CyclingApplication) getApplication()).inject(this);

        setContentView(R.layout.activity_cycle_map);
        setUpMapIfNeeded();

        mList = new Select().from(PoiList.class).limit(1).executeSingle();
        long oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        boolean listOutdated = mList == null || mList.getUpdatedAt() < oneDayAgo;
        if (listOutdated) {
            mCyclingService.pointsOfInterest(new Callback<PoiList>() {
                @Override
                public void success(PoiList poiList, Response response) {
                    // Delete old list
                    if (mList != null) {
                        mList.delete();
                    }

                    // Fetch new
                    mList = poiList;
                    mList.setUpdatedAt(System.currentTimeMillis());
                    ActiveAndroid.beginTransaction();
                    try {
                        mList.save();
                        for (Poi poi : mList.getLocations()) {
                            poi.setPoiList(mList);
                            poi.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    } finally {
                        ActiveAndroid.endTransaction();
                    }
                    showLocations();
                }

                @Override
                public void failure (RetrofitError error){
                    Toast.makeText(getBaseContext(), "Network error retrieving locations",
                            Toast.LENGTH_LONG).show();

                    // Fall back to old list if possible
                    if (mList != null) {
                        showLocations();
                    }
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
                    .getExtendedMap();

            ClusterOptionsProvider clusterOptions = new ClusterOptionsProvider() {
                @Override
                public ClusterOptions getClusterOptions(List<Marker> markers) {
                    int clusterId = markers.get(0).getClusterGroup();
                    String typeName = mTypes.get(clusterId);
                    int drawableId = getBaseContext().getResources().getIdentifier(typeName + "_cluster", "drawable",
                            getBaseContext().getPackageName());
                    return new ClusterOptions()
                            .icon(BitmapDescriptorFactory.fromResource(drawableId));
                }
            };
            ClusteringSettings clusterSettings = new ClusteringSettings()
                    .enabled(true)
                    .clusterOptionsProvider(clusterOptions);
            mMap.setClustering(clusterSettings);
        }
    }

    public void showLocations() {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // Clear map
                mMap.clear();
                mTypes = new ArrayList<String>();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Poi poi : mList.getLocations()) {
                    LatLng location = new LatLng(poi.getLat(), poi.getLng());
                    String type = poi.getType();

                    MarkerOptions options = new MarkerOptions()
                            .position(location);

                    // marker icon
                    int drawableId = getBaseContext().getResources().getIdentifier(type, "drawable",
                            getBaseContext().getPackageName());
                    options.icon(BitmapDescriptorFactory.fromResource(drawableId));

                    // marker title
                    if (poi.getName() != null && poi.getName().length() > 0) {
                        options.title(poi.getName());
                    } else {
                        String capitalizedType = type.toUpperCase().charAt(0) + type.substring(1);
                        options.title(capitalizedType);
                    }

                    // marker type
                    if (!mTypes.contains(type)) {
                        mTypes.add(type);
                    }
                    int typeId = mTypes.indexOf(type);
                    options.clusterGroup(typeId);

                    mMap.addMarker(options);
                    builder.include(options.getPosition());
                }

                // Zoom to show all POI
                LatLngBounds bounds = builder.build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                mMap.animateCamera(cameraUpdate);
            }
        });
    }
}
