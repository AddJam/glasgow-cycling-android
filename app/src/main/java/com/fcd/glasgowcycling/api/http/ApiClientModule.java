package com.fcd.glasgowcycling.api.http;

import android.content.Context;
import android.util.Log;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.activities.RouteCaptureActivity;
import com.fcd.glasgowcycling.activities.RouteListActivity;
import com.fcd.glasgowcycling.activities.SignInActivity;
import com.fcd.glasgowcycling.api.AuthModel;
import com.fcd.glasgowcycling.activities.UserOverviewActivity;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.client.UrlConnectionClient;
import retrofit.converter.GsonConverter;

/**
 * Created by chrissloey on 01/07/2014.
 */
@Module(complete=false, library = true, injects = {
        SignInActivity.class,
        UserOverviewActivity.class,
        ApiClientModule.class,
        RouteListActivity.class,
        RouteCaptureActivity.class
})
public class ApiClientModule {

    private final String TAG = "ApiClientModule";

    private final String ENDPOINT = "http://192.168.1.38:3000"; // "http://10.0.2.2:3000" == Localhost (for simulator)
    private Context mContext;
    private CyclingApplication mApplication;
    private AuthModel mAuthModel;
    private GoCyclingApiInterface sAuthService;

    public ApiClientModule(Context context, CyclingApplication app) {
        mApplication = app;
        mContext = context;
        mAuthModel = new AuthModel(mContext);
        sAuthService = provideAuthClient();
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
                .setClient(new CyclingClient())
                .build();

        return restAdapter.create(GoCyclingApiInterface.class);
    }

    /*
     * Client which doesn't handle errors, for requests which shouldn't result in logout on 401
     */
    private GoCyclingApiInterface provideAuthClient() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .build();

        return restAdapter.create(GoCyclingApiInterface.class);
    }

    private class GoCyclingAPIRequestInterceptor implements RequestInterceptor {

        @Override
        public void intercept(RequestInterceptor.RequestFacade request) {
            request.addQueryParam("client_id", "123");
            request.addQueryParam("client_secret", "321");

            mAuthModel.updateTokens();
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
                mApplication.logout();
            }
            return cause;
        }
    }

    private class CyclingClient extends UrlConnectionClient {
        @Override
        public Response execute(Request request) throws IOException {
            Response response = super.execute(request);
            if (response.getStatus() == 401) {
                // Unauthorized, attempt to refresh access token
                String refreshToken = mAuthModel.getRefreshToken();
                if (refreshToken == null) {
                    return response;
                }
                mAuthModel = sAuthService.refreshToken(refreshToken);
                mAuthModel.setContext(mContext);
                mAuthModel.saveTokens();
                Log.d(TAG, "Refresh token is now " + mAuthModel.getRefreshToken());
                Log.d(TAG, "Access token is now " + mAuthModel.getUserToken());

                // Switch out auth token in request
                List<Header> headers = new ArrayList<Header>();
                for (Header header : request.getHeaders()) {
                    if (header.getName().equals("Authorization")) {
                        Log.d(TAG, "Replace auth bearer");
                        headers.add(new Header("Authorization", "Bearer " + mAuthModel.getUserToken()));
                    } else {
                        headers.add(header);
                    }
                }

                // Retry request
                Request updatedRequest = new Request(request.getMethod(), request.getUrl(), headers, request.getBody());
                return super.execute(updatedRequest);
            } else {
                return response;
            }
        }
    }
}
