package com.fcd.glasgowcycling.api.http;

import com.fcd.glasgowcycling.api.AuthModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;

public class OAuthClient extends OkClient {

    private AuthModel mAuthModel;

    @Inject
    GoCyclingApiInterface cyclingService;

    public OAuthClient(AuthModel authModel){
        mAuthModel = authModel;
    }

    /**
     * Replaces the access token with the new one in the request.
     * @param request Input request to modify
     * @return An other request with the same parameters, but the replaced authorization header
     */
    private Request changeTokenInRequest(Request request){
        List<Header> tempHeaders = request.getHeaders(); // this one is an unmodifiable list.
        List<Header> headers = new ArrayList<Header>();
        headers.addAll(tempHeaders); // this one is modifiable
        Iterator<Header> iter = headers.iterator();
        boolean hadAuthHeader = false;
        // we check if there was an authentication header in the original request
        while(iter.hasNext()){
            Header h = iter.next();
            if (h.getName().equals("Authorization")){
                iter.remove();
                hadAuthHeader = true;
            }
        }
        // if there was an authentication header, replace it with another one containing the new access token.
        if (hadAuthHeader){
            headers.add(new Header("Authorization", "Bearer " + mAuthModel.getUserToken()));
        }
        // everything stays the same, except the headers
        return new Request(request.getMethod(), request.getUrl(), headers, request.getBody());
    }

    @Override
    public Response execute(Request request) throws IOException {
        Response response = super.execute(request);
        // 401: Forbidden, 403: Permission denied
        if (response.getStatus() == 401 || response.getStatus() == 403) {
            // the next call should be a synchronous call, otherwise it will immediately continue, and use the old token instead.
            mAuthModel = cyclingService.refreshToken();
            // the headers should be modified because the access token changed
            Request newRequest = changeTokenInRequest(request);
            return super.execute(newRequest);
        } else {
            return response;
        }
    }
}
