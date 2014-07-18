package com.fcd.glasgowcycling.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

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
