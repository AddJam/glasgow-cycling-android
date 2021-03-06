package com.fcd.glasgow_cycling.api.http;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.fcd.glasgow_cycling.BuildConfig;
import com.fcd.glasgow_cycling.CyclingApplication;
import com.fcd.glasgow_cycling.activities.AccountForgottenActivity;
import com.fcd.glasgow_cycling.activities.AccountPasswordActivity;
import com.fcd.glasgow_cycling.activities.AccountSettingsActivity;
import com.fcd.glasgow_cycling.activities.CycleMapActivity;
import com.fcd.glasgow_cycling.activities.RouteCaptureActivity;
import com.fcd.glasgow_cycling.activities.RouteListActivity;
import com.fcd.glasgow_cycling.activities.RouteOverviewActivity;
import com.fcd.glasgow_cycling.activities.SearchActivity;
import com.fcd.glasgow_cycling.activities.SignInActivity;
import com.fcd.glasgow_cycling.activities.SignUpActivity;
import com.fcd.glasgow_cycling.activities.UserOverviewActivity;
import com.fcd.glasgow_cycling.activities.UserStatsActivity;
import com.fcd.glasgow_cycling.api.responses.AuthModel;
import com.fcd.glasgow_cycling.api.routes.RouteSearch;
import com.fcd.glasgow_cycling.utils.AddJam;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by chrissloey on 01/07/2014.
 */
@Module(complete=false, library = true, injects = {
        SignInActivity.class,
        UserOverviewActivity.class,
        ApiClientModule.class,
        RouteListActivity.class,
        RouteSearch.class,
        RouteCaptureActivity.class,
        SignUpActivity.class,
        SearchActivity.class,
        RouteOverviewActivity.class,
        AccountSettingsActivity.class,
        AccountPasswordActivity.class,
        AccountForgottenActivity.class,
        CycleMapActivity.class,
        UserStatsActivity.class
})
public class ApiClientModule {

    private final String TAG = "ApiClientModule";
    private final String ENDPOINT;
    private Context mContext;
    private CyclingApplication mApplication;
    private AuthModel mAuthModel;
    private GoCyclingApiInterface sAuthService;

    public ApiClientModule(Context context, CyclingApplication app) {
        if (BuildConfig.LOCAL_MODE) {
            ENDPOINT = "http://192.168.56.1:3000";// (for simulator)
        } else {
            ENDPOINT = "https://glasgowcycling.com/";
        }
        mApplication = app;
        mContext = context;
        mAuthModel = new AuthModel(mContext);
        sAuthService = provideAuthClient();
    }

    @Provides
    public GoCyclingApiInterface provideClient() {
        OkHttpClient httpClient = new OkHttpClient();
        CyclingClient client = new CyclingClient(httpClient);

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new GoCyclingAPIRequestInterceptor())
                .setErrorHandler(new GoCyclingAPIErrorHandler())
                .setClient(client)
                .build();

        return restAdapter.create(GoCyclingApiInterface.class);
    }

    /*
     * Client which doesn't handle errors, for requests which shouldn't result in logout on 401
     */
    public GoCyclingApiInterface provideAuthClient() {
        OkHttpClient httpClient = new OkHttpClient();
        OkClient client = new OkClient(httpClient);

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .setClient(client)
                .build();

        return restAdapter.create(GoCyclingApiInterface.class);
    }

    private class GoCyclingAPIRequestInterceptor implements RequestInterceptor {

        @Override
        public void intercept(RequestInterceptor.RequestFacade request) {
            if (BuildConfig.LOCAL_MODE) {
                request.addQueryParam("client_id", "123");
                request.addQueryParam("client_secret", "321");
            } else {
                request.addQueryParam("client_id", "3db23ee6dfb278fafb78f6cd3c5f2140ebbce0f2cde3a3fb612f669bb879b0c4");
                request.addQueryParam("client_secret", "8cb0159073b4df229a32f88e32ead76aa77608a606b3c69a77e36b4341ca2b6a");
            }

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
                AddJam.log(Log.DEBUG, TAG, "Network error making request: " + cause.getUrl());
            } else if (cause.getResponse() == null) {
                AddJam.log(Log.DEBUG, TAG, "Error making request");
            } else if (cause.getResponse().getStatus() == 401) {
                // Refresh token and try again
                AddJam.log(Log.DEBUG, TAG, "Unauthorized error making request, logging out");
                mApplication.logout();
            }
            return cause;
        }
    }

    private class CyclingClient extends OkClient {
        public CyclingClient(OkHttpClient client) {
            super (client);
        }

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

                // Switch out auth token in request
                List<Header> headers = new ArrayList<Header>();
                for (Header header : request.getHeaders()) {
                    if (header.getName().equals("Authorization")) {
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
