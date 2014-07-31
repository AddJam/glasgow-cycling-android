package com.fcd.glasgowcycling.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Route implements Serializable {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @SerializedName("start_name")
    @Expose
    private String startName;
    @SerializedName("end_name")
    @Expose
    private String endName;
    @SerializedName("start_maidenhead")
    @Expose
    private String startMaidenhead;
    @SerializedName("end_maidenhead")
    @Expose
    private String endMaidenhead;
    @SerializedName("last_route_time")
    @Expose
    private Integer lastRouteTime;
    @SerializedName("num_instances")
    @Expose
    private Integer numInstances;
    @SerializedName("num_reviews")
    @Expose
    private Integer numReviews;
    @Expose
    private Averages averages;

    @Expose
    private List<Point> points = new ArrayList<Point>();

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public String getStartMaidenhead() {
        return startMaidenhead;
    }

    public void setStartMaidenhead(String startMaidenhead) {
        this.startMaidenhead = startMaidenhead;
    }

    public String getEndMaidenhead() {
        return endMaidenhead;
    }

    public void setEndMaidenhead(String endMaidenhead) {
        this.endMaidenhead = endMaidenhead;
    }

    public Integer getLastRouteTime() {
        return lastRouteTime;
    }

    public void setLastRouteTime(Integer lastRouteTime) {
        this.lastRouteTime = lastRouteTime;
    }

    public Integer getNumInstances() {
        return numInstances;
    }

    public void setNumInstances(Integer numInstances) {
        this.numInstances = numInstances;
    }

    public Integer getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(Integer numReviews) {
        this.numReviews = numReviews;
    }

    public Averages getAverages() {
        return averages;
    }

    public void setAverages(Averages averages) {
        this.averages = averages;
    }

}