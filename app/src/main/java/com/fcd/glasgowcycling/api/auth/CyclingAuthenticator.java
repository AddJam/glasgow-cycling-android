package com.fcd.glasgowcycling.api.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.TextUtils;
import android.widget.Toast;

import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.activities.SignInActivity;
import com.fcd.glasgowcycling.api.AuthModel;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;

import javax.inject.Inject;

/**
 * Created by chrissloey on 03/07/2014.
 */
public class CyclingAuthenticator extends AbstractAccountAuthenticator {
    private Context mContext;
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public final String ACCOUNT_TYPE = "com.fcd.GlasgowCycling";
    public static final int ERROR_CODE_ONE_ACCOUNT_ALLOWED = 100;

    @Inject
    GoCyclingApiInterface cyclingService;

    public CyclingAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        AccountManager accountManager = AccountManager.get(mContext);
        Account[] existingAccounts = accountManager.getAccountsByType(ACCOUNT_TYPE);

        // Only allow one account
        if (existingAccounts.length == 0) {
            final Intent intent = new Intent(mContext, SignInActivity.class);
            intent.putExtra(SignInActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        } else {
            final Bundle bundle = new Bundle();
            final String errorString = mContext.getString(R.string.one_account_allowed);
            bundle.putInt(AccountManager.KEY_ERROR_CODE, CyclingAuthenticator.ERROR_CODE_ONE_ACCOUNT_ALLOWED);
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE, errorString);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(mContext, errorString, Toast.LENGTH_LONG);
                    toast.show();
                }
            });

            return bundle;
        }
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = am.peekAuthToken(account, authTokenType);
        String refreshToken = am.peekAuthToken(account, KEY_REFRESH_TOKEN);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                AuthModel auth = cyclingService.signin(account.name, password);
                authToken = auth.getUserToken();
                refreshToken = auth.getRefreshToken();
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken) && !TextUtils.isEmpty(refreshToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            result.putString(KEY_REFRESH_TOKEN, refreshToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, SignInActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
