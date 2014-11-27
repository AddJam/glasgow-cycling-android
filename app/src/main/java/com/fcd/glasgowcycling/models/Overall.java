package com.fcd.glasgowcycling.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * Created by michaelhayes on 27/11/14.
 */
@Table(name = "Overall")
public class Overall extends Model {
    @Expose
    @Column(name = "Distance")
    private double distance;
    @Expose @Column(name = "AvgSpeed")
    private double avgSpeed;
    @Expose @Column(name = "MinSpeed")
    private double minSpeed;
    @Expose @Column(name = "RoutesStarted")
    private int routesStarted;
    @Expose @Column(name = "RoutesCompleted")
    private int routesCompleted;


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    public int getRoutesStarted() {
        return routesStarted;
    }

    public void setRoutesStarted(int routesStarted) {
        this.routesStarted = routesStarted;
    }

    public int getRoutesCompleted() {
        return routesCompleted;
    }

    public void setRoutesCompleted(int routesCompleted) {
        this.routesCompleted = routesCompleted;
    }
}
