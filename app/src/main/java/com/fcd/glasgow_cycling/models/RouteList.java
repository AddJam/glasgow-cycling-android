package com.fcd.glasgow_cycling.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class RouteList {

    @Expose
    private List<Route> routes = new ArrayList<Route>();

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

}
