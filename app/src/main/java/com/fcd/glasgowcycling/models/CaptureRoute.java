package com.fcd.glasgowcycling.models;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by michaelhayes on 10/07/2014.
 */
public class CaptureRoute {

    private ArrayList<CapturePoints> pointsArray = new ArrayList<CapturePoints>();
    private long startTime;
    private float distance = 0;
    private double avgSpeed;
    private final String TAG = "Capture";

    public CaptureRoute(){

    }

    public void addRoutePoint(Location location, String streetname){
        //increment distance and workout avg speed
        if (pointsArray.size() != 0) {
            CapturePoints lastPoint = pointsArray.get(pointsArray.size() - 1);

            //for doing distanceTo()
            //TODO look into accuracy being high before incrementing
            Location source = new Location("fcd");
            source.setLatitude(lastPoint.getLat());
            source.setLongitude(lastPoint.getLng());

            Log.d(TAG, "Location with accuracy " + location.getAccuracy() + " and time " + location.getTime() + " and speed " + location.getSpeed());
            distance = distance + source.distanceTo(location);
            avgSpeed = 0;
            for (int i = 0; i < pointsArray.size(); i++) {
                avgSpeed = avgSpeed + pointsArray.get(i).getKph();
            }
        }
        avgSpeed = avgSpeed/pointsArray.size();
        //if avgSpeed is too low set to 0, stop minus speed showing
        if (avgSpeed < 1){
            avgSpeed = 0;
        }

        CapturePoints newPoint = new CapturePoints();
        newPoint.setAltitude(location.getAltitude());
        newPoint.setCourse(location.getBearing());
        newPoint.setLat(location.getLatitude());
        newPoint.setLng(location.getLongitude());
        newPoint.setTime(System.currentTimeMillis());
        newPoint.setHorizontalAccuracy(location.getAccuracy());
        newPoint.setVerticalAccuracy(-1); // iOS gives this Android doesnt, set to minus 1 here
        newPoint.setKph(location.getSpeed());
        newPoint.setStreet_name(streetname);

        pointsArray.add(newPoint);

    }

    public ArrayList<CapturePoints> getPointsArray() {
        return pointsArray;
    }

    public void setPointsArray(ArrayList<CapturePoints> pointsArray) {
        this.pointsArray = pointsArray;
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

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
}
