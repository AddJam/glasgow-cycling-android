package com.fcd.glasgowcycling.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by michaelhayes on 29/07/2014.
 */

public class Weather {

    @Expose
    private Integer time;
    @Expose
    private String icon;
    @SerializedName("precipitation_probability")
    @Expose
    private Double precipitationProbability;
    @SerializedName("precipitation_type")
    @Expose
    private String precipitationType;
    @Expose
    private Double temp;
    @SerializedName("wind_speed")
    @Expose
    private Double windSpeed;
    @SerializedName("wind_bearing")
    @Expose
    private Integer windBearing;
    @Expose
    private String source;

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getIcon() {
        return icon;
    }

    public String getUseableIcon() {
        String usable = icon.replace('-','_');
        return usable;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getPrecipitationProbability() {
        return precipitationProbability;
    }

    public String getReadablePrecipitationProbability() {
        return String.format("%.0f",(precipitationProbability * 100)) + "%";
    }

    public void setPrecipitationProbability(Double precipitationProbability) {
        this.precipitationProbability = precipitationProbability;
    }

    public String getPrecipitationType() {
        return precipitationType;
    }

    public void setPrecipitationType(String precipitationType) {
        this.precipitationType = precipitationType;
    }

    public Double getTemp() {
        return temp;
    }

    public String getReadableTemp() {
        return String.format("%.1f", ((temp - 32)/1.8)) + "°C";
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public String getReadableWindSpeed() {
        return String.format("%.0f", windSpeed) + " mph";
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Integer getWindBearing() {
        return windBearing;
    }

    public void setWindBearing(Integer windBearing) {
        this.windBearing = windBearing;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}