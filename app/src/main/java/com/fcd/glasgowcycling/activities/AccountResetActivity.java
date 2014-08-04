package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class AccountResetActivity extends Activity {

    @Inject
    GoCyclingApiInterface cyclingService;
    private static final String TAG = "AccountSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_reset);
        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);
    }

}
