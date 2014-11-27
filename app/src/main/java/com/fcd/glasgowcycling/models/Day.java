package com.fcd.glasgowcycling.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by michaelhayes on 19/11/14.
 */
@Table(name = "Day")
public class Day {

    @Expose @Column(name = "Distance")
    private double distance;

    @SerializedName("avg_speed")
    @Expose @Column(name = "AvgSpeed")
    private double avgSpeed;
    @SerializedName("min_speed")
    @Expose @Column(name = "MinSpeed")
    private double minSpeed;
    @SerializedName("max_speed")
    @Expose @Column(name = "MaxSpeed")
    private double maxSpeed;
    @SerializedName("routes_started")
    @Expose @Column(name = "RoutesStarted")
    private Integer routesStarted;
    @SerializedName("routes_completed")
    @Expose @Column(name = "RoutesCompleted")
    private Integer routesCompleted;
    @Expose @Column(name = "Time")
    private Integer time;

    /**
     *
     * @return
     * The distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     * The distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     * The avgSpeed
     */
    public double getAvgSpeed() {
        return avgSpeed;
    }

    /**
     *
     * @param avgSpeed
     * The avg_speed
     */
    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    /**
     *
     * @return
     * The minSpeed
     */
    public double getMinSpeed() {
        return minSpeed;
    }

    /**
     *
     * @param minSpeed
     * The min_speed
     */
    public void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }

    /**
     *
     * @return
     * The maxSpeed
     */
    public double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     *
     * @param maxSpeed
     * The max_speed
     */
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     *
     * @return
     * The routesStarted
     */
    public Integer getRoutesStarted() {
        return routesStarted;
    }

    /**
     *
     * @param routesStarted
     * The routes_started
     */
    public void setRoutesStarted(Integer routesStarted) {
        this.routesStarted = routesStarted;
    }

    /**
     *
     * @return
     * The routesCompleted
     */
    public Integer getRoutesCompleted() {
        return routesCompleted;
    }

    /**
     *
     * @param routesCompleted
     * The routes_completed
     */
    public void setRoutesCompleted(Integer routesCompleted) {
        this.routesCompleted = routesCompleted;
    }

    /**
     *
     * @return
     * The time
     */
    public Integer getTime() {
        return time;
    }

    /**
     *
     * @param time
     * The time
     */
    public void setTime(Integer time) {
        this.time = time;
    }

}
