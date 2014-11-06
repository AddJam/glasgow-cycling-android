package com.fcd.glasgowcycling.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;


/**
 * Created by michaelhayes on 29/07/2014.
 */

@Table(name = "Weathers")
public class Weather extends Model {

    @Expose @Column(name = "Time")
    private Integer time;

    @Expose @Column(name = "Icon")
    private String icon;

    @Expose @Column(name = "PrecipitationProbability")
    private Double precipitationProbability;

    @Expose @Column(name = "PrecipitationType")
    private String precipitationType;

    @Expose @Column(name = "Temp")
    private Double temp;

    @Expose @Column(name = "WindSpeed")
    private Double windSpeed;

    @Expose @Column(name = "WindBearing")
    private Integer windBearing;

    @Expose @Column(name = "Source")
    private String source;

    public Weather() {
        super();
    }

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