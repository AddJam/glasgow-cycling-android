package com.fcd.glasgowcycling.models;

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import com.google.gson.annotations.Expose;

/**
 * Created by michaelhayes on 19/11/14.
 */
@Table(name = "Overall")
public class Overall extends Model {

    @Expose
    private Overall overall;
    @Expose
    private List<Day> days = new ArrayList<Day>();

    @Expose @Column(name = "Distance")
    private double distance;
    @Expose @Column(name = "AvgSpeed")
    private double avgSpeed;
    @Expose @Column(name = "MinSpeed")
    private double minSpeed;
    @Expose @Column(name = "RoutesStarted")
    private double routesStarted;
    @Expose @Column(name = "RoutesCompleted")
    private double routesCompleted;


    /**
     *
     * @return
     * The overall
     */
    public Overall getOverall() {
        return overall;
    }

    /**
     *
     * @param overall
     * The overall
     */
    public void setOverall(Overall overall) {
        this.overall = overall;
    }

    /**
     *
     * @return
     * The days
     */
    public List<Day> getDays() {
        return days;
    }

    /**
     *
     * @param days
     * The hours
     */
    public void setDays(List<Day> days) {
        this.days = days;
    }

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

    public double getRoutesStarted() {
        return routesStarted;
    }

    public void setRoutesStarted(double routesStarted) {
        this.routesStarted = routesStarted;
    }

    public double getRoutesCompleted() {
        return routesCompleted;
    }

    public void setRoutesCompleted(double routesCompleted) {
        this.routesCompleted = routesCompleted;
    }
}
