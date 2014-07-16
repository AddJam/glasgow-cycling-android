package com.fcd.glasgowcycling.models;

import android.text.format.Time;

import java.util.ArrayList;

/**
 * Created by michaelhayes on 10/07/2014.
 */
public class Route {

    private ArrayList<RoutePoint> pointsArray = new ArrayList<RoutePoint>();
    private long startTime;
    private double distance;

    public Route(){

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

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
