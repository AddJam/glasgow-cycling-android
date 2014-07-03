package com.fcd.glasgowcycling.api.http;

import com.fcd.glasgowcycling.activities.SignInActivity;
import com.fcd.glasgowcycling.activities.UserOverviewActivity;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by chrissloey on 01/07/2014.
 */
@Module(complete=false, library = true, injects = {
        SignInActivity.class,
        UserOverviewActivity.class
})
public class ApiClientModule {

    @Provides
    public GoCyclingApiInterface provideClient() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        final RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint("http://10.0.2.2:3000") // Localhost (for simulator)
                .setEndpoint("http://192.168.10.76:3000") // Tethered IP (device)
                .setConverter(new GsonConverter(gson))
                .build();

        return restAdapter.create(GoCyclingApiInterface.class);
    }
}
