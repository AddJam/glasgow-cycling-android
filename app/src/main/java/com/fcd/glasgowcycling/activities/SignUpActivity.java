package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;

import com.fcd.glasgowcycling.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignUpActivity extends Activity {

    private static final String TAG = "SignUpActivity";

    @InjectView(R.id.first_name) EditText firstNameField;
    @InjectView(R.id.last_name) EditText lastNameField;
    @InjectView(R.id.email) EditText emailField;
    @InjectView(R.id.password) EditText passwordField;
    @InjectView(R.id.gender_button) Button genderButton;
    @InjectView(R.id.year_of_birth_button) Button yearOfBirthButton;
    @InjectView(R.id.picture_button) Button pictureButton;
    @InjectView(R.id.submit_button) Button submitButton;

    private static final int SELECT_PHOTO = 100;

    //contents of form
    private Bitmap userSelectedImage;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String gender;
    private String yearOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);

        genderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Gender clicked");
                genderPicker();
            }
        });

        yearOfBirthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Year of Birth clicked");
                yearPicker();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Submit sign up clicked");
            }
        });

        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Picture button clicked");
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

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

    public void genderPicker(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.gender_dialog);
        dialog.setTitle("Select a gender");
        dialog.setCancelable(true);

        Button undisclosedButton = (Button) dialog.findViewById(R.id.undisclosed_button);
        Button femaleButton = (Button) dialog.findViewById(R.id.female_button);
        Button maleButton = (Button) dialog.findViewById(R.id.male_button);

        undisclosedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Undisclosed Gender selected");
                gender = "Undisclosed";
                genderButton.setText(gender);
                dialog.dismiss();
            }
        });
        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Female Gender selected");
                gender = "Female";
                genderButton.setText(gender);
                dialog.dismiss();
            }
        });
        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Male Gender selected");
                gender = "Male";
                genderButton.setText(gender);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void yearPicker(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.year_picker_dialog);
        dialog.setTitle("Select a year of birth");
        dialog.setCancelable(true);

        Button closeButton = (Button) dialog.findViewById(R.id.close_button);
        NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.year_picker);

        String[] values = new String[100];
        int value = 1900;
        numberPicker.setMinValue(value);

        for(int i=0;i<values.length;i++){
            values[i]=Integer.toString(value);
            value++;
        }

        numberPicker.setMaxValue(value - 1);
        numberPicker.setDisplayedValues(values);
        numberPicker.setValue(1990);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                String[] values=numberPicker.getDisplayedValues();
                yearOfBirth = String.valueOf(i2);
                yearOfBirthButton.setText(yearOfBirth);
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Year of birth selected");
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    userSelectedImage = BitmapFactory.decodeStream(imageStream);
                    Drawable drawableImage = new BitmapDrawable(getResources(),userSelectedImage);
                    pictureButton.setBackground(drawableImage);
                }
        }
    }

    private void getSignupInputs(){
        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        email = emailField.getText().toString();
        password = passwordField.getText().toString();
    }
}
