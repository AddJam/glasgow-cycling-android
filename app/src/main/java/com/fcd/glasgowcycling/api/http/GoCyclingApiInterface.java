package com.fcd.glasgowcycling.api.http;

import com.fcd.glasgowcycling.api.AuthModel;

import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by chrissloey on 01/07/2014.
 */
public interface GoCyclingApiInterface {
    @POST("/oauth/token?grant_type=password" +
            "&client_id=913623efb87a2efa99860b74f0ee7d63c25a750efcd9d93bb62a177add26eccb" +
            "&client_secret=84980d8bb9440cba2b3709ce30958e6c11da1e22a3475c4327a498c16b62fe0b")
    AuthModel signin(@Query("email") String email, @Query("password") String password);

    @POST("/oauth/token?grant_type=refresh_token" +
            "&client_id=913623efb87a2efa99860b74f0ee7d63c25a750efcd9d93bb62a177add26eccb" +
            "&client_secret=84980d8bb9440cba2b3709ce30958e6c11da1e22a3475c4327a498c16b62fe0b")
    AuthModel refreshToken();
}
