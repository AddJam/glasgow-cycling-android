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
    private Double rating;

    public Double getDistance() {
        return distance;
    }

    public Double getDistanceMiles() {
        return distance * 0.621371192;
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

    public Double getRating() {
        if (rating == null) {
            return 0.0;
        }
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

}