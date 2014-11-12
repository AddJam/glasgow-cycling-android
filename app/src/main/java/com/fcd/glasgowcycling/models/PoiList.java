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
    List<Poi> locations;

    @Column(name="updatedAt")
    long updatedAt;

    public PoiList() {
        super();
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Poi> getLocations() {
        if (locations != null) {
            return locations;
        } else {
            return getMany(Poi.class, "PoiList");
        }
    }

    public void setLocations(List<Poi> locations) {
        this.locations = locations;
    }
}
