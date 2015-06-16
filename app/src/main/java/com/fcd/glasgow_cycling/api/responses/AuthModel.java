package com.fcd.glasgow_cycling.api.responses;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.fcd.glasgow_cycling.api.auth.CyclingAuthenticator;
import com.google.gson.annotations.Expose;

/**
 * Created by chrissloey on 01/07/2014.
 */
public class AuthModel {

    private final String TAG = "AuthModel";

    @Expose
    private String accessToken;

    @Expose
    private String refreshToken;

    private Context mContext;

    public AuthModel(Context context) {
        mContext = context;
        updateTokens();
    }

    public void saveTokens() {
        AccountManager accountManager = AccountManager.get(mContext);
        Account[] accountList = accountManager.getAccountsByType(CyclingAuthenticator.ACCOUNT_TYPE);
        if (accountList.length > 0) {
            Account userAccount = accountList[0];
            accountManager.setAuthToken(userAccount, AccountManager.KEY_AUTHTOKEN, accessToken);
            accountManager.setAuthToken(userAccount, CyclingAuthenticator.KEY_REFRESH_TOKEN, refreshToken);
        }
    }

    public void updateTokens() {
        AccountManager accountManager = AccountManager.get(mContext);
        Account[] accountList = accountManager.getAccountsByType(CyclingAuthenticator.ACCOUNT_TYPE);
        if (accountList.length > 0) {
            Account userAccount = accountList[0];
            String authToken = accountManager.peekAuthToken(userAccount, AccountManager.KEY_AUTHTOKEN);
            String refreshToken = accountManager.peekAuthToken(userAccount, CyclingAuthenticator.KEY_REFRESH_TOKEN);
            setUserToken(authToken);
            setRefreshToken(refreshToken);
            Crashlytics.log(Log.DEBUG, TAG, "User tokens initialized");
        }
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public String getUserToken() {
        return accessToken;
    }

    public void setUserToken(String userToken) {
        this.accessToken = userToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
