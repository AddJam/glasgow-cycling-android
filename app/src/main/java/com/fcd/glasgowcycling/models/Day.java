package com.fcd.glasgowcycling.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by michaelhayes on 19/11/14.
 */
public class Day {

    @Expose
    private Integer distance;
    @SerializedName("avg_speed")
    @Expose
    private Integer avgSpeed;
    @SerializedName("min_speed")
    @Expose
    private Integer minSpeed;
    @SerializedName("max_speed")
    @Expose
    private Integer maxSpeed;
    @SerializedName("routes_started")
    @Expose
    private Integer routesStarted;
    @SerializedName("routes_completed")
    @Expose
    private Integer routesCompleted;
    @Expose
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
