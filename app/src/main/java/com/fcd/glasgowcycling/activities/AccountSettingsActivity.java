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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.models.User;
import com.fcd.glasgowcycling.utils.ActionBarFontUtil;
import com.fcd.glasgowcycling.utils.ImageUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AccountSettingsActivity extends Activity {

    @Inject
    GoCyclingApiInterface cyclingService;
    private static final String TAG = "AccountSettings";

    @InjectView(R.id.settings_username) EditText usernameField;
    @InjectView(R.id.settings_email) EditText emailField;
    @InjectView(R.id.settings_picture_button) ImageView pictureButton;
    @InjectView(R.id.settings_gender_button) Button genderButton;
    @InjectView(R.id.settings_submit_button) Button submitButton;
    @InjectView(R.id.settings_logout_button) Button logoutButton;
    @InjectView(R.id.progress) SmoothProgressBar progressBar;

    private User mUser;

    private static final int SELECT_PHOTO = 100;
    private boolean pictureUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        ((CyclingApplication) getApplication()).inject(this);
        ButterKnife.inject(this);
        ActionBarFontUtil.setFont(this);

        mUser = new Select().from(User.class).limit(1).executeSingle();

        usernameField.setText(mUser.getUsername());
        emailField.setText(mUser.getEmail());

        if (mUser.getProfilePic() != null){
            String encodedUserImage = mUser.getProfilePic().replace("data:image/jpeg;base64,", "");
            byte[] decodedString = Base64.decode(encodedUserImage, Base64.NO_WRAP);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            pictureButton.setImageBitmap(decodedImage);
        }
        genderButton.setText(mUser.getGender());

        genderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Gender clicked");
                genderPicker();
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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactivateButtons();
                Log.d(TAG, "Submit settings clicked");
                //submit settings
                submitAccountDetails();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactivateButtons();
                Log.d(TAG, "Logout clicked");
                ((CyclingApplication)getApplication()).logout();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_change_password) {
            startActivity(new Intent(getApplicationContext(), AccountPasswordActivity.class));
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
                genderButton.setText("Undisclosed");
                dialog.dismiss();
            }
        });
        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Female Gender selected");
                genderButton.setText("Female");
                dialog.dismiss();
            }
        });
        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Male Gender selected");
                genderButton.setText("Male");
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
                    Bitmap userSelectedImage = ImageUtil.getImage(getBaseContext(), selectedImage, 400, 400);

                    if (userSelectedImage == null) {
                        Toast.makeText(getBaseContext(),
                                "Image couldn't be found",
                                Toast.LENGTH_SHORT)
                            .show();
                        return;
                    }

                    // Show image
                    Drawable drawableImage = new BitmapDrawable(getResources(), userSelectedImage);
                    pictureButton.setImageDrawable(drawableImage);
                    pictureUpdate = true;
                }
        }
    }

    private void submitAccountDetails(){
        mUser.setUsername(usernameField.getText().toString());
        mUser.setEmail(emailField.getText().toString());
        mUser.setGender(genderButton.getText().toString());
        if (pictureUpdate){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap userImage = ((BitmapDrawable)pictureButton.getDrawable()).getBitmap();
            if (userImage != null) {
                userImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String base64img = "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);
                base64img = base64img.replaceAll("\n", "");
                mUser.setProfilePic(base64img);
            }
        }

        progressBar.setVisibility(View.VISIBLE);
        cyclingService.updateDetails(mUser, new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        Log.d(TAG, "User details updated");
                        new Delete().from(User.class).execute();

                        // Store
                        mUser = user;
                        mUser.getMonth().save();
                        mUser.save();
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "Failed to update user details");
                        progressBar.setVisibility(View.GONE);
                        submitButton.setEnabled(true);
                        activateButtons();
                        Toast.makeText(getApplicationContext(),
                                "Sorry, failed to update your details",
                                Toast.LENGTH_LONG)
                            .show();
                    }
                }
        );
    }

    public void activateButtons(){
        submitButton.setEnabled(true);
        logoutButton.setEnabled(true);
        genderButton.setEnabled(true);
        pictureButton.setEnabled(true);
    }

    public void deactivateButtons(){
        submitButton.setEnabled(false);
        logoutButton.setEnabled(false);
        genderButton.setEnabled(false);
        pictureButton.setEnabled(false);
    }
}
