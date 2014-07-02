package com.fcd.glasgowcycling.api.http;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by chrissloey on 01/07/2014.
 */
public class ApiClient {

    public static GoCyclingApiInterface getClient() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        final RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint("http://10.0.2.2:3000") // Localhost (for simulator)
                .setEndpoint("http://172.20.10.8:3000") // Tethered IP (device)
                .setConverter(new GsonConverter(gson))
                .build();

        return restAdapter.create(GoCyclingApiInterface.class);
    }
}
