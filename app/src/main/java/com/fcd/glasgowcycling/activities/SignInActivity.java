package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.AuthModel;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignInActivity extends Activity {

    private static final String TAG = "SignInActivity";

    // Bundle Args
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "ADDING_NEW_ACCOUNT";

    // Views
    @InjectView(R.id.email) AutoCompleteTextView emailField;
    @InjectView(R.id.password) EditText passwordField;
    @InjectView(R.id.email_sign_in_button) Button signInButton;

    // API
    @Inject GoCyclingApiInterface cyclingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.inject(this);
        ((CyclingApplication) getApplication()).inject(this);

        emailField.setText("chris.sloey@gmail.com");
        passwordField.setText("password");

        signInButton.setOnClickListener(new SignInListener(cyclingService));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SignInListener implements View.OnClickListener {

        private GoCyclingApiInterface cyclingService;

        public SignInListener(GoCyclingApiInterface cyclingService) {
            this.cyclingService = cyclingService;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Sign In clicked");
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            cyclingService.signin(email, password, new Callback<AuthModel>() {
                @Override
                public void success(AuthModel authModel, Response response) {
                    Log.d(TAG, "Logged in! auth token is " + authModel.getUserToken());
                    startActivity(new Intent(getApplicationContext(), UserOverviewActivity.class));
                    finish();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "Failed to login");
                }
            });
        }
    }
}
