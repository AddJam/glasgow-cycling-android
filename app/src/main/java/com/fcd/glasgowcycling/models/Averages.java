package com.fcd.glasgowcycling.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Averages implements Serializable {

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

    public String getReadableDistance() {
        return String.format("%.02f", getDistanceMiles()) + " miles";
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getTime() {
        return time;
    }

    public String getReadableTime() {
        int hours = (int) Math.floor(time / 3600);
        int minutes = (int) Math.floor((time / 60) - (3600 * hours));
        int seconds = (int) Math.floor(time - (3600 * hours) * (60 * minutes));

        if (hours > 0) {
            String descriptor = "hours";
            if (minutes == 1) {
                descriptor = "hour";
            }
            return String.format("%02d:%02d:%02d %s", descriptor, hours, minutes, seconds);
        } else if (minutes > 0) {
            String descriptor = "minutes";
            if (minutes == 1) {
                descriptor = "minute";
            }
            return String.format("%02d:%02d %s", descriptor, minutes, seconds);
        } else {
            return String.format("%02d seconds", seconds);
        }
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Double getSpeed() {
        return speed;
    }

    public Double getSpeedMph() {
        Double kmPerHour = (speed / 1000) * 3600;
        return kmPerHour * 0.621371192;
    }

    public String getReadableSpeed() {
        return String.format("%.02f", getSpeedMph()) + " mph";
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