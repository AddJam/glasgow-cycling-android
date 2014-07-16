package com.fcd.glasgowcycling.models;

/**
 * Created by michaelhayes on 10/07/2014.
 */
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.types.TimeStampType;
import com.j256.ormlite.table.DatabaseTable;
import com.google.gson.annotations.Expose;

@DatabaseTable(tableName = "RoutePoint")
public class RoutePoint {

    @DatabaseField(generatedId = true)
    private int id;

    @Expose
    @DatabaseField
    private long time;

    @Expose
    @DatabaseField
    private float speed;

    @Expose
    @DatabaseField
    private double altitude;

    @Expose
    @DatabaseField
    private float course;

    @Expose
    @DatabaseField
    private double lat;

    @Expose
    @DatabaseField
    private double lng;

    @Expose
    @DatabaseField
    private float hAccuracy;

    @Expose
    @DatabaseField
    private float vAccuracy;

    public RoutePoint() {

    }

    public RoutePoint(long time, float speed, float altitude, float course, double lat, double lng, float hAccuracy, float vAccuracy) {
        this.time = time;
        this.speed = speed;
        this.altitude = altitude;
        this.course = course;
        this.lat = lat;
        this.lng = lng;
        this.hAccuracy = hAccuracy;
        this.vAccuracy = vAccuracy;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getCourse() {
        return course;
    }

    public void setCourse(float course) {
        this.course = course;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public float gethAccuracy() {
        return hAccuracy;
    }

    public void sethAccuracy(float hAccuracy) {
        this.hAccuracy = hAccuracy;
    }

    public float getvAccuracy() {
        return vAccuracy;
    }

    public void setvAccuracy(float vAccuracy) {
        this.vAccuracy = vAccuracy;
    }

    public int getId() {
        return this.id;
    }

}
