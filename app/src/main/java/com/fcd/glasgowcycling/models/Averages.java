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
        int totalMinutes = (int) Math.floor(time / 60);
        int minutes = totalMinutes % 60;
        int hours = (int)Math.floor(totalMinutes / 60);

        String hoursDescriptor = "hours";
        if (hours == 1) {
            hoursDescriptor = "hour";
        }

        String minutesDescriptor = "minutes";
        if (minutes == 1) {
            minutesDescriptor = "minute";
        }

        if (hours > 0) {
            return String.format("%02d %s %02d %s", hours, hoursDescriptor, minutes, minutesDescriptor);
        } else {
            return String.format("%02d %s", minutes, minutesDescriptor);
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