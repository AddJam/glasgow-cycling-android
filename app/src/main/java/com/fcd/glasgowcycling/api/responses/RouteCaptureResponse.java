package com.fcd.glasgowcycling.api.responses;

import com.google.gson.annotations.Expose;

public class RouteCaptureResponse {
    @Expose
    private Integer routeId;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }
}
