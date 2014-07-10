package com.fcd.glasgowcycling.models;
import com.google.gson.annotations.Expose;

/**
 * Created by michaelhayes on 03/07/2014.
 */
public class Month {

    @Expose
    private Integer total;
    @Expose
    private Double km;
    @Expose
    private Integer seconds;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Double getKm() {
        return km;
    }

    public Double getMiles() {
        return km * 0.621371192;
    }

    public void setKm(Double km) {
        this.km = km;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public Integer getHours() {
        return (int)Math.floor(seconds / 3600);
    }

    public Integer getMinutes() {
        int minutesInSeconds = seconds - (getHours() * 3600);
        return (int)Math.floor(minutesInSeconds / 60);
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public String getReadableDistance() {
        if (getMinutes() == 0) {
            return String.format("%d hours this month", getHours());
        } else if (getMinutes() < 60) {
            return String.format("%d minutes this month", getMinutes());
        } else {
            return String.format("%02d:%02d hours this month", getHours(), getMinutes());
        }
    }

    public String getReadableTime() {
        if (getMiles() == 0) {
            return String.format("%.0f miles this month", getMiles());
        } else {
            return String.format("%.01f miles this month", getMiles());
        }
    }
}
