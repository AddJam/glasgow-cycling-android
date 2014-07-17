package com.fcd.glasgowcycling.models;

import android.location.Location;
import android.text.format.Time;

import java.util.ArrayList;

/**
 * Created by michaelhayes on 10/07/2014.
 */
public class Route {

    private ArrayList<RoutePoint> pointsArray = new ArrayList<RoutePoint>();
    private long startTime;
    private float distance;
    private double avgSpeed;

    public Route(){

    }

    public void addRoutePoint(Location location){
        if (pointsArray.size() != 0) {
            RoutePoint lastPoint = pointsArray.get(pointsArray.size() - 1);
            float[] dist = new float[2];
            location.distanceBetween(lastPoint.getLat()*1E6, lastPoint.getLng()*1E6, location.getLatitude()*1E6, location.getLongitude()*1E6, dist);

            distance = distance + dist[0];
            avgSpeed = 0;
            for (int i = 0; i < pointsArray.size(); i++) {
                avgSpeed = avgSpeed + pointsArray.get(i).getSpeed();
            }
        }

        avgSpeed = avgSpeed/pointsArray.size();

        if (avgSpeed < 1){
            avgSpeed = 0;
        }

        RoutePoint newPoint = new RoutePoint();
        newPoint.setAltitude(location.getAltitude());
        newPoint.setCourse(location.getBearing());
        newPoint.setLat(location.getLatitude());
        newPoint.setLng(location.getLongitude());
        newPoint.setTime(System.currentTimeMillis());
        newPoint.sethAccuracy(location.getAccuracy());
        newPoint.sethAccuracy(location.getAccuracy());
        newPoint.setSpeed(location.getSpeed());

        pointsArray.add(newPoint);

    }

    public ArrayList<RoutePoint> getPointsArray() {
        return pointsArray;
    }

    public void setPointsArray(ArrayList<RoutePoint> pointsArray) {
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
