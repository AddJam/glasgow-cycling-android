package com.fcd.glasgowcycling.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by chrissloey on 06/11/2014.
 */
@Table(name="PoiLists")
public class PoiList extends Model {
    @Expose
    @Column(name="locations")
    List<Poi> locations;

    public List<Poi> getLocations() {
        return locations;
    }

    public void setLocations(List<Poi> locations) {
        this.locations = locations;
    }
}
