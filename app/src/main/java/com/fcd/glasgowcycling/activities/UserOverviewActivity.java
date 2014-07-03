package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.AuthResult;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.Month;
import com.fcd.glasgowcycling.models.User;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserOverviewActivity extends Activity {
    @Inject private GoCyclingApiInterface cyclingService;
    private static final String TAG = "OverviewActivity";

    @InjectView(R.id.username) TextView username;
    @InjectView(R.id.distance_stat) TextView distanceStat;
    @InjectView(R.id.time_stat) TextView timeStat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_overview);
        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);

        getDetails();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_overview, menu);
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

    private void getDetails(){
        cyclingService.details(new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Log.d(TAG, "retreived user details for " + user.getUserID());
                populateFields(user);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to login");
            }
        });
    }

    private void populateFields(User user){
        Month month = user.getMonth();

        username.setText(user.getFirstName() + "" + user.getLastName());
        distanceStat.setText(String.valueOf(month.getKm()));
        timeStat.setText(month.getSeconds());
    }
}
