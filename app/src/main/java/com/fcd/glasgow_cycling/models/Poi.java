package com.fcd.glasgow_cycling.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chrissloey on 06/11/2014.
 */
@Table(name = "Pois")
public class Poi extends Model {
    @Expose @Column(name="lat")
    float lat;

    @Expose @SerializedName("long") @Column(name="lng")
    float lng;

    @Expose @Column(name="name")
    String name;

    @Expose @Column(name="type")
    String type;

    @Column(name="PoiList", onDelete= Column.ForeignKeyAction.CASCADE)
    PoiList poiList;

    public Poi() {
        super();
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PoiList getPoiList() {
        return poiList;
    }

    public void setPoiList(PoiList poiList) {
        this.poiList = poiList;
    }
}
