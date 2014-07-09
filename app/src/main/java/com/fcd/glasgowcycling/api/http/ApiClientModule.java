package com.fcd.glasgowcycling.api.http;

import android.content.Context;
import android.util.Log;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.activities.SignInActivity;
import com.fcd.glasgowcycling.api.AuthModel;
import com.fcd.glasgowcycling.activities.UserOverviewActivity;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.converter.GsonConverter;

/**
 * Created by chrissloey on 01/07/2014.
 */
@Module(complete=false, library = true, injects = {
        SignInActivity.class,
        UserOverviewActivity.class,
        ApiClientModule.class
})
public class ApiClientModule {

    private final String TAG = "ApiClientModule";
    private final String ENDPOINT = "http://172.20.10.8:3000"; // "http://10.0.2.2:3000" == Localhost (for simulator)
    private Context mContext;
    private AuthModel mAuthModel;
    GoCyclingApiInterface sRefreshService;

    public ApiClientModule(Context context) {
        mContext = context;
        mAuthModel = new AuthModel(mContext);
        sRefreshService = provideAuthClient();
    }

    @Provides
    public GoCyclingApiInterface provideClient() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new GoCyclingAPIRequestInterceptor())
                .setErrorHandler(new GoCyclingAPIErrorHandler())
                .build();

        return restAdapter.create(GoCyclingApiInterface.class);
    }

    /*
     * Client which doesn't handle errors (preventing recursion when refreshing in error handler)
     */
    private GoCyclingApiInterface provideAuthClient() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("client_id", "123");
                        request.addQueryParam("client_secret", "321");

                        String refreshToken = mAuthModel.getRefreshToken();
                        if (refreshToken != null) {
                            request.addQueryParam("refresh_token", refreshToken);
                        }
                    }
                })
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        Log.d(TAG, "Error refreshing token");
                        if (cause.isNetworkError()) {
                            // Can retry refreshing
                            Log.d(TAG, "Network error refreshing token");
                        } else if (cause.getResponse().getStatus() == 401) {
                            // Unauthorized - refresh failed, logout
                            // TODO logout user
                            Log.d(TAG, "Logging out, refresh token failed");
                        }
                        return cause;
                    }
                })
                .build();

        return restAdapter.create(GoCyclingApiInterface.class);
    }

    private class GoCyclingAPIRequestInterceptor implements RequestInterceptor {

        @Override
        public void intercept(RequestInterceptor.RequestFacade request) {
            request.addQueryParam("client_id", "123");
            request.addQueryParam("client_secret", "321");

            String userToken = mAuthModel.getUserToken();
            if (userToken != null) {
                request.addHeader("Authorization", "Bearer " + userToken);
            }
        }
    }

    private class GoCyclingAPIErrorHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError cause) {
            if (cause.isNetworkError()) {
                Log.d(TAG, "Network error making request: " + cause.getUrl());
            } else if (cause.getResponse().getStatus() == 401) {
                // Refresh token and try again
                Log.d(TAG, "Unauthorized error making request");
                Log.d(TAG, "Refreshing token using " + mAuthModel.getRefreshToken());
                Log.d(TAG, "Access token was " + mAuthModel.getUserToken());
                try {
                    mAuthModel = sRefreshService.refreshToken();
                    Log.d(TAG, "Refresh token is now " + mAuthModel.getRefreshToken());
                    Log.d(TAG, "Access token is now " + mAuthModel.getUserToken());
                } catch (RetrofitError error) {
                    Log.d(TAG, "Error refreshing token");
                }
            }
            return cause;
        }
    }
}
