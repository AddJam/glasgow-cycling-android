package com.fcd.glasgow_cycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.fcd.glasgow_cycling.CyclingApplication;
import com.fcd.glasgow_cycling.R;
import com.fcd.glasgow_cycling.api.http.ApiClientModule;
import com.fcd.glasgow_cycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgow_cycling.api.responses.AuthModel;
import com.fcd.glasgow_cycling.utils.ActionBarFontUtil;
import com.fcd.glasgow_cycling.utils.AddJam;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AccountPasswordActivity extends Activity {

    @InjectView(R.id.password_current) EditText currentField;
    @InjectView(R.id.password_new) EditText newField;
    @InjectView(R.id.password_repeat) EditText repeatField;
    @InjectView(R.id.password_submit_button) Button submitButton;
    private GoCyclingApiInterface cyclingService;
    private final String TAG = "Password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_password);
        ButterKnife.inject(this);
        ActionBarFontUtil.setFont(this);

        cyclingService = new ApiClientModule(this, (CyclingApplication)getApplication()).provideAuthClient();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddJam.log(Log.INFO, TAG, "Password submission clicked");
                submitButton.setEnabled(false);
                submitAccountDetails();
            }
        });
    }


    private void submitAccountDetails(){

        String currentPassword = currentField.getText().toString();
        String newPassword = newField.getText().toString();
        String repeatPassword = repeatField.getText().toString();

        if (!newPassword.equals(repeatPassword)){
            Toast.makeText(getApplicationContext(),"New password fields do not match", Toast.LENGTH_LONG).show();
            submitButton.setEnabled(true);
            AddJam.log(Log.INFO, TAG, "Passwords don't match");
            return;
        }

        if (newPassword.equals(currentPassword)){
            Toast.makeText(getApplicationContext(),"Your old password and new password are the same", Toast.LENGTH_LONG).show();
            submitButton.setEnabled(true);
            AddJam.log(Log.INFO, TAG, "New and old password are the same");
            return;
        }

        if (newPassword.length() < 8){
            Toast.makeText(getApplicationContext(),"Password must be longer than 8 characters", Toast.LENGTH_LONG).show();
            submitButton.setEnabled(true);
            AddJam.log(Log.INFO, TAG, "New password too short");
            return;
        }

        cyclingService.resetPassword(currentPassword,newPassword, new Callback<AuthModel>() {
            @Override
            public void success(AuthModel authModel, Response response) {
                AddJam.log(Log.INFO, TAG, "Successfully updated password");
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(),"Unable to reset password, ensure current password is correct", Toast.LENGTH_LONG).show();
                submitButton.setEnabled(true);
                AddJam.log(Log.INFO, TAG, "Failed to update password");
            }
        });
    }

}
