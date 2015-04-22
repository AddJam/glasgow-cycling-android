package com.fcd.glasgow_cycling.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Point {

    @Expose
    private Integer id;
    @SerializedName("route_id")
    @Expose
    private Integer routeId;
    @Expose
    private Double lat;
    @SerializedName("long")
    @Expose
    private Double _long;
    @Expose
    private Double altitude;
    @SerializedName("on_road")
    @Expose
    private Object onRoad;
    @SerializedName("street_name")
    @Expose
    private String streetName;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @Expose
    private String time;
    @SerializedName("is_important")
    @Expose
    private Boolean isImportant;
    @Expose
    private Double kph;
    @SerializedName("vertical_accuracy")
    @Expose
    private Double verticalAccuracy;
    @SerializedName("horizontal_accuracy")
    @Expose
    private Double horizontalAccuracy;
    @Expose
    private Double course;
    @Expose
    private String maidenhead;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public LatLng getLatLng() {
        return new LatLng(getLat(), getLong());
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLong() {
        return _long;
    }

    public void setLong(Double _long) {
        this._long = _long;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Object getOnRoad() {
        return onRoad;
    }

    public void setOnRoad(Object onRoad) {
        this.onRoad = onRoad;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getIsImportant() {
        return isImportant;
    }

    public void setIsImportant(Boolean isImportant) {
        this.isImportant = isImportant;
    }

    public Double getKph() {
        return kph;
    }

    public void setKph(Double kph) {
        this.kph = kph;
    }

    public Double getVerticalAccuracy() {
        return verticalAccuracy;
    }

    public void setVerticalAccuracy(Double verticalAccuracy) {
        this.verticalAccuracy = verticalAccuracy;
    }

    public Double getHorizontalAccuracy() {
        return horizontalAccuracy;
    }

    public void setHorizontalAccuracy(Double horizontalAccuracy) {
        this.horizontalAccuracy = horizontalAccuracy;
    }

    public Double getCourse() {
        return course;
    }

    public void setCourse(Double course) {
        this.course = course;
    }

    public String getMaidenhead() {
        return maidenhead;
    }

    public void setMaidenhead(String maidenhead) {
        this.maidenhead = maidenhead;
    }

}
