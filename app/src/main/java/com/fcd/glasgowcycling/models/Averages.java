package com.fcd.glasgowcycling.models;

import com.google.gson.annotations.Expose;

public class Averages {

    @Expose
    private Double distance;
    @Expose
    private Double time;
    @Expose
    private Double speed;
    @Expose
    private Object rating;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Object getRating() {
        return rating;
    }

    public void setRating(Object rating) {
        this.rating = rating;
    }

}