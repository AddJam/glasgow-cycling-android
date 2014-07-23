package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.fcd.glasgowcycling.R;

import butterknife.InjectView;

public class SignUpActivity extends Activity {

    @InjectView(R.id.first_name) EditText firstNameField;
    @InjectView(R.id.last_name) EditText lastNameField;
    @InjectView(R.id.email) EditText emailField;
    @InjectView(R.id.password) EditText passwordField;
    @InjectView(R.id.gender_button) Button genderButton;
    @InjectView(R.id.year_of_birth_button) Button yearOfBirthButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
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
