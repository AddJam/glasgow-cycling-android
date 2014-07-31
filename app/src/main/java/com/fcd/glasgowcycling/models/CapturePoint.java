package com.fcd.glasgowcycling.models;

/**
 * Created by michaelhayes on 10/07/2014.
 */
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

@Table(name = "CapturePoints")
public class CapturePoint extends Model {

    @Column(name = "PointId")
    @SerializedName("id")
    private int pointId;

    @Expose
    @Column(name = "Time")
    private long time;

    @Expose
    @Column(name = "Kph")
    private float kph;

    @Expose
    @Column(name = "Altitude")
    private double altitude;

    @Expose
    @Column(name = "Course")
    private float course;

    @Expose
    @Column(name = "Lat")
    private double lat;

    @Expose
    @SerializedName("long")
    @Column(name = "Lng")
    private double lng;

    @Expose
    @Column(name = "HorizontalAccuracy")
    private float horizontalAccuracy;

    @Expose
    @Column(name = "VerticalAccuracy")
    private float verticalAccuracy;

    public CapturePoint() {
        super();
    }

    public CapturePoint(long time, float kph, float altitude, float course, double lat, double lng, float horizontalAccuracy, float verticalAccuracy) {
        super();
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

    public int getPointId() {
        return this.pointId;
    }

}
