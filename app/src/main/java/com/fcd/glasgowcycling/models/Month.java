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

    public void setKm(Double km) {
        this.km = km;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }


}
