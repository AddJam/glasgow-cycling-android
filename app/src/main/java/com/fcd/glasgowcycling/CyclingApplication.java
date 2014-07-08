package com.fcd.glasgowcycling;

import android.app.Application;

import com.fcd.glasgowcycling.api.http.ApiClientModule;

import dagger.ObjectGraph;

/**
 * Created by chrissloey on 02/07/2014.
 */
public class CyclingApplication extends Application {
    private ObjectGraph graph;

    @Override
    public void onCreate() {
        super.onCreate();

        graph = ObjectGraph.create(getModules());
    }

    private Object[] getModules() {
        return new Object[] { new ApiClientModule(getApplicationContext()) };
    }

    public void inject(Object target) {
        graph.inject(target);
    }
}
