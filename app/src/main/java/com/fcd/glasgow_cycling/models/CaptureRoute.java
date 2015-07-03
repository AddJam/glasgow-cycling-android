package com.fcd.glasgow_cycling.models;

import android.location.Location;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.crashlytics.android.Crashlytics;
import com.fcd.glasgow_cycling.utils.AddJam;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelhayes on 10/07/2014.
 */
@Table(name = "CaptureRoutes")
public class CaptureRoute extends Model {

    @Expose
    private List<CapturePoint> points = new ArrayList<CapturePoint>();

    private long startTime;

    @Column(name = "distance")
    private float distance = 0;

    private double avgSpeed;
    private final String TAG = "Capture";

    public CaptureRoute(){
        super();
    }

    public CapturePoint addRoutePoint(Location location){
        //increment distance and workout avg speed
        double combinedSpeed = 0;
        if (points.size() != 0) {
            CapturePoint lastPoint = points.get(points.size() - 1);
            Location source = new Location("fcd");
            source.setLatitude(lastPoint.getLat());
            source.setLongitude(lastPoint.getLng());

            AddJam.log(Log.DEBUG, TAG, "Adding location to CaptureRoute with accuracy " + location.getAccuracy() + " and time " + location.getTime() + " and speed " + location.getSpeed());
            distance = distance + source.distanceTo(location);
            combinedSpeed = 0;
            for (int i = 0; i < points.size(); i++) {
                combinedSpeed = combinedSpeed + points.get(i).getKph();
            }
        }
        avgSpeed = combinedSpeed/ points.size();
        //if avgSpeed is too low set to 0, stop minus speed showing
        if (avgSpeed < 0 || points.size() == 0){
            avgSpeed = 0;
        }

        CapturePoint newPoint = new CapturePoint();
        newPoint.setAltitude(location.getAltitude());
        newPoint.setCourse(location.getBearing());
        newPoint.setLat(location.getLatitude());
        newPoint.setLng(location.getLongitude());
        newPoint.setTime(System.currentTimeMillis() / 1000L);
        newPoint.setHorizontalAccuracy(location.getAccuracy());
        newPoint.setVerticalAccuracy(-1); // iOS gives this Android doesnt, set to minus 1 here
        newPoint.setKph(location.getSpeed());

        points.add(newPoint);

        newPoint.captureRoute = this;
        newPoint.save();
        return newPoint;
    }

    public List<CapturePoint> getPoints() {
        return points;
    }

    public void setPoints(List<CapturePoint> points) {
        this.points = points;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public double getAvgSpeedMiles() {
        return avgSpeed * 2.2369362920544;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public List<CapturePoint> points() {
        return getMany(CapturePoint.class, "CaptureRoute");
    }
}
