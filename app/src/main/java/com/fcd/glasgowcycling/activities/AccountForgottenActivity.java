package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.User;
import com.fcd.glasgowcycling.utils.ActionBarFontUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AccountForgottenActivity extends Activity {

    @Inject
    GoCyclingApiInterface cyclingService;
    private static final String TAG = "ForgotPassword";

    @InjectView(R.id.reset_email) EditText emailField;
    @InjectView(R.id.submit_button) Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_forgotten);
        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);
        ActionBarFontUtil.setFont(this);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Submit email clicked");
                //submit settings
                submitReset();
                submitButton.setEnabled(false);
            }
        });
    }

    private void submitReset(){
        String email = emailField.getText().toString();
        Pattern pattern = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,10}");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()){
            Toast.makeText(getApplicationContext(), "Please enter a valid email", Toast.LENGTH_LONG).show();
            return;
        }
         cyclingService.forgottenPassword(email, new Callback<User>() {
             @Override
             public void success(User user, Response response) {
                 Toast.makeText(getApplicationContext(), "Instructions sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
                 Log.d(TAG, "instructions sent");
                 finish();
             }

             @Override
             public void failure(RetrofitError error) {
                 Toast.makeText(getApplicationContext(), "Hmmm, are you sure you're registered?", Toast.LENGTH_LONG).show();
                 Log.d(TAG, "unable to send reset instructions");
                 submitButton.setEnabled(true);
             }
         });

    }

}
