package com.fcd.glasgowcycling.models;

/**
 * Created by michaelhayes on 10/07/2014.
 */
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.google.gson.annotations.Expose;

@DatabaseTable(tableName = "CapturePoints")
public class CapturePoints {

    @DatabaseField(generatedId = true)
    private int id;

    @Expose
    @DatabaseField
    private long time;

    @Expose
    @DatabaseField
    private float kph;

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
    @SerializedName("long")
    @DatabaseField
    private double lng;

    @Expose
    @DatabaseField
    private float horizontalAccuracy;

    @Expose
    @DatabaseField
    private float verticalAccuracy;

    public CapturePoints() {

    }

    public CapturePoints(long time, float kph, float altitude, float course, double lat, double lng, float horizontalAccuracy, float verticalAccuracy) {
        this.time = time;
        this.kph = kph;
        this.altitude = altitude;
        this.course = course;
        this.lat = lat;
        this.lng = lng;
        this.horizontalAccuracy = horizontalAccuracy;
        this.verticalAccuracy = verticalAccuracy;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getKph() {
        return kph;
    }

    public void setKph(float kph) {
        this.kph = kph;
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

    public float getHorizontalAccuracy() {
        return horizontalAccuracy;
    }

    public void setHorizontalAccuracy(float horizontalAccuracy) {
        this.horizontalAccuracy = horizontalAccuracy;
    }

    public float getVerticalAccuracy() {
        return verticalAccuracy;
    }

    public void setVerticalAccuracy(float verticalAccuracy) {
        this.verticalAccuracy = verticalAccuracy;
    }

    public int getId() {
        return this.id;
    }

}
