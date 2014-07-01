package com.fcd.glasgowcycling.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.ApiClient;
import com.fcd.glasgowcycling.api.AuthResult;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class SignInActivity extends RoboActivity {

    private static final String TAG = "SignInActivity";

    @InjectView(R.id.email)
    AutoCompleteTextView emailField;

    @InjectView(R.id.password)
    EditText passwordField;

    @InjectView(R.id.email_sign_in_button)
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailField.setText("chris.sloey@gmail.com");
        passwordField.setText("password");

        final GoCyclingApiInterface cyclingService = ApiClient.getClient();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign In clicked");
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                cyclingService.signin(email, password, new Callback<AuthResult>() {
                    @Override
                    public void success(AuthResult authResult, Response response) {
                        Log.d(TAG, "Logged in! auth token is " + authResult.getUserToken());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "Failed to login");
                    }
                });
            }
        });
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
}
