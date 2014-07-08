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
                .setEndpoint("http://192.168.10.34:3000") // Tethered IP (device)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("client_id", "913623efb87a2efa99860b74f0ee7d63c25a750efcd9d93bb62a177add26eccb");
                        request.addQueryParam("client_secret", "84980d8bb9440cba2b3709ce30958e6c11da1e22a3475c4327a498c16b62fe0b");
                    }
                })
//                .setClient(oAuthClient)
                .build();

        return restAdapter.create(GoCyclingApiInterface.class);
    }
}
