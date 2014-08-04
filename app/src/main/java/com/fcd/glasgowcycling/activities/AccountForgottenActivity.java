package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AccountForgottenActivity extends Activity {

    @Inject
    GoCyclingApiInterface cyclingService;
    private static final String TAG = "AccountSettings";

    @InjectView(R.id.reset_email) EditText emailField;
    @InjectView(R.id.submit_button)Button submitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_reset);
        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Submit settings clicked");
                //submit settings
                submitReset();
            }
        });
    }

    private void submitReset(){

    }

}
