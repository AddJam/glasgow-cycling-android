package com.fcd.glasgowcycling.activities;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CycleMapActivity extends FragmentActivity {

    @Inject
    GoCyclingApiInterface mCyclingService;

    @InjectView(R.id.progress) SmoothProgressBar progressBar;

    private GoogleMap mMap;
    private PoiList mList;
    private List<String> mTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_map);
        ButterKnife.inject(this);
        ((CyclingApplication) getApplication()).inject(this);
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
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void failure (RetrofitError error){
                    Toast.makeText(getBaseContext(), "Network error retrieving locations",
                            Toast.LENGTH_LONG).show();

                    // Fall back to old list if possible
                    if (mList != null) {
                        showLocations();
                    }
                    progressBar.setVisibility(View.GONE);
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
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                private final View window = getLayoutInflater().inflate(R.layout.map_info_window, null);

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoWindow(Marker marker) {
                    TextView view = (TextView)window.findViewById(R.id.title);
                    String title = marker.getTitle();
                    if (title != null && title.length() > 0) {
                        view.setText(title);
                    } else {
                        int typeId = marker.getClusterGroup();
                        String type = mTypes.get(typeId);
                        type = type.toUpperCase().charAt(0) + type.substring(1) + "s";
                        view.setText(type);
                    }
                    return view;
                }
            });

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

            // Show Glasgow area
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(55.8628, -4.235), 10.8f));
        }
    }

    public void showLocations() {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // Clear map
                mMap.clear();
                mTypes = new ArrayList<String>();
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
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
