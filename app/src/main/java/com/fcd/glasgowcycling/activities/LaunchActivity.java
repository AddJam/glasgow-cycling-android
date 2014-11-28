package com.fcd.glasgowcycling.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.crashlytics.android.Crashlytics;
import com.fcd.glasgowcycling.utils.ActionBarFontUtil;

public class LaunchActivity extends Activity {

    private AccountManager mAccountManager;
    public final String ACCOUNT_TYPE = "com.fcd.GlasgowCycling";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        ActionBarFontUtil.setFont(this);

        mAccountManager = AccountManager.get(this);
        Account[] userAccounts = mAccountManager.getAccountsByType(ACCOUNT_TYPE);
        if (userAccounts.length > 0) {
            startActivity(new Intent(this, UserOverviewActivity.class));
        } else {
            startActivity(new Intent(this, WelcomeActivity.class));
        }
        finish();
    }
}