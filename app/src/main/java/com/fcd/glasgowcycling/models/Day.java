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
    private Integer distance;

    @SerializedName("avg_speed")
    @Expose @Column(name = "AvgSpeed")
    private Integer avgSpeed;
    @SerializedName("min_speed")
    @Expose @Column(name = "MinSpeed")
    private Integer minSpeed;
    @SerializedName("max_speed")
    @Expose @Column(name = "MaxSpeed")
    private Integer maxSpeed;
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
    public Integer getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     * The distance
     */
    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     * The avgSpeed
     */
    public Integer getAvgSpeed() {
        return avgSpeed;
    }

    /**
     *
     * @param avgSpeed
     * The avg_speed
     */
    public void setAvgSpeed(Integer avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    /**
     *
     * @return
     * The minSpeed
     */
    public Integer getMinSpeed() {
        return minSpeed;
    }

    /**
     *
     * @param minSpeed
     * The min_speed
     */
    public void setMinSpeed(Integer minSpeed) {
        this.minSpeed = minSpeed;
    }

    /**
     *
     * @return
     * The maxSpeed
     */
    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    /**
     *
     * @param maxSpeed
     * The max_speed
     */
    public void setMaxSpeed(Integer maxSpeed) {
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
