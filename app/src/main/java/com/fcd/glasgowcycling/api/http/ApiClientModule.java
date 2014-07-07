package com.fcd.glasgowcycling.api.http;

import com.fcd.glasgowcycling.activities.SignInActivity;
import com.fcd.glasgowcycling.api.AuthModel;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by chrissloey on 01/07/2014.
 */
@Module(complete=false, library = true, injects = {
        SignInActivity.class,
        AuthModel.class
})
public class ApiClientModule {

    @Provides
    public GoCyclingApiInterface provideClient() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

//        AuthModel authModel = new AuthModel();
//        OAuthClient oAuthClient = new OAuthClient(authModel);

        final RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint("http://10.0.2.2:3000") // Localhost (for simulator)
                .setEndpoint("http://172.20.10.5:3000") // Tethered IP (device)
                .setConverter(new GsonConverter(gson))
//                .setClient(oAuthClient)
                .build();

        return restAdapter.create(GoCyclingApiInterface.class);
    }
}
