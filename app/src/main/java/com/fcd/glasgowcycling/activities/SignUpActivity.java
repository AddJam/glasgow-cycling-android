package com.fcd.glasgowcycling.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.LoadingView;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.auth.CyclingAuthenticator;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.api.requests.SignupRequest;
import com.fcd.glasgowcycling.api.responses.AuthModel;
import com.fcd.glasgowcycling.utils.ActionBarFontUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignUpActivity extends Activity {

    @Inject
    GoCyclingApiInterface cyclingService;

    private static final String TAG = "SignUpActivity";

    @InjectView(R.id.username) EditText usernameField;
    @InjectView(R.id.email) EditText emailField;
    @InjectView(R.id.password) EditText passwordField;
    @InjectView(R.id.gender_button) Button genderButton;
    @InjectView(R.id.year_of_birth_button) Button yearOfBirthButton;
    @InjectView(R.id.picture_button) ImageView pictureButton;
    @InjectView(R.id.submit_button) Button submitButton;
    @InjectView(R.id.loading_view) LoadingView loadingView;

    private static final int SELECT_PHOTO = 100;

    // contents of form
    private Bitmap userSelectedImage;
    private String gender;
    private String yearOfBirth;

    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);
        ((CyclingApplication) getApplication()).inject(this);
        ActionBarFontUtil.setFont(this);

        mAccountManager = AccountManager.get(this);

        loadingView.setBlue(true);

        // Check for email from sign in form
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("email")) {
            emailField.setText(extras.getString("email"));
        }

        // Pickers
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

        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Picture button clicked");
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        // Submit
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Submit sign up clicked");
                // Dismiss keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(imm.isAcceptingText()) { // verify if the soft keyboard is open
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                // Submit
                submitButton.setEnabled(false);
                loadingView.startAnimating();
                submitSignup();
            }
        });
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
                    if (userSelectedImage.getWidth() > 400 || userSelectedImage.getHeight() > 400){
                        userSelectedImage = Bitmap.createBitmap(userSelectedImage, (userSelectedImage.getWidth() / 2) - 200, (userSelectedImage.getHeight() / 2) - 200, 400, 400);
                    }
                    Drawable drawableImage = new BitmapDrawable(getResources(), userSelectedImage);
                    pictureButton.setImageDrawable(drawableImage);
                }
        }
    }

    public void showToast(final String message, final int length) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(SignUpActivity.this, message, length).show();
            }
        });
    }

    public void signupFailed() {
        runOnUiThread(new Runnable() {
            public void run() {
                submitButton.setEnabled(true);
                endLoading();
            }
        });
    }

    public void endLoading() {
        runOnUiThread(new Runnable() {
            public void run() {
                loadingView.stopAnimating();
            }
        });
    }

    private void submitSignup(){
        String username = usernameField.getText().toString();
        final String email = emailField.getText().toString();
        final String password = passwordField.getText().toString();
        String profilePic = "";
        if (userSelectedImage != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            userSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            profilePic = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        Log.d(TAG, "Base 64 img >> "+ profilePic);
        AuthModel authModel;

        cyclingService.signup(new SignupRequest(email, username, password, gender, yearOfBirth, profilePic),
                new Callback<AuthModel>() {
            @Override
            public void success(AuthModel authModel, Response response) {
                endLoading();
                final Account account = new Account(email, CyclingAuthenticator.ACCOUNT_TYPE);
                // Creating the account on the device and setting the auth token we got
                // (Not setting the auth token will cause another call to the server to authenticate the user)
                mAccountManager.addAccountExplicitly(account, password, null);
                mAccountManager.setAuthToken(account, AccountManager.KEY_AUTHTOKEN, authModel.getUserToken());
                mAccountManager.setAuthToken(account, CyclingAuthenticator.KEY_REFRESH_TOKEN, authModel.getRefreshToken());

                Intent userIntent = new Intent(getBaseContext(), UserOverviewActivity.class);
                userIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(userIntent);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Retrofit error");
                if (error.isNetworkError()) {
                    Log.d(TAG, "Network error");
                    showToast("Check your connection and try again", Toast.LENGTH_SHORT);
                } else if (error.getResponse().getStatus() == 401) {
                    // Unauthorized
                    Log.d(TAG, "Invalid details");
                } else {
                    showToast("Sign up failed - are you already signed up?", Toast.LENGTH_LONG);
                }
                signupFailed();
            }
        });
    }
}
