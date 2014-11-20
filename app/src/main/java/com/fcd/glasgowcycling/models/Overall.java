package com.fcd.glasgowcycling.models;

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.Model;
import com.google.gson.annotations.Expose;

import com.google.gson.annotations.Expose;

/**
 * Created by michaelhayes on 19/11/14.
 */
public class Overall extends Model {

    @Expose
    private Overall overall;
    @Expose
    private List<Day> days = new ArrayList<Day>();

    /**
     *
     * @return
     * The overall
     */
    public Overall getOverall() {
        return overall;
    }

    /**
     *
     * @param overall
     * The overall
     */
    public void setOverall(Overall overall) {
        this.overall = overall;
    }

    /**
     *
     * @return
     * The days
     */
    public List<Day> getDays() {
        return days;
    }

    /**
     *
     * @param days
     * The hours
     */
    public void setDays(List<Day> days) {
        this.days = days;
    }
}
