package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcd.glasgowcycling.R;

import butterknife.InjectView;

public class RouteCapture extends Activity {

    @InjectView(R.id.distance_info) TextView distanceInfo;
    @InjectView(R.id.time_info) TextView timeInfo;
    @InjectView(R.id.avg_speed_info) TextView avgSpeedInfo;
    @InjectView(R.id.speed_info) TextView speedInfo;
    @InjectView(R.id.finish_button) Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_capture);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.route_capture, menu);
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
