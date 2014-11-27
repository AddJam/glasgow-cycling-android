package com.fcd.glasgowcycling.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.requests.SignupRequest;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WelcomeActivity extends Activity {

    @InjectView(R.id.sign_in_button) Button signInButton;
    @InjectView(R.id.sign_up_button) Button signUpButton;
    @InjectView(R.id.explain_text) TextView explainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.inject(this);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = new Intent(getBaseContext(), SignInActivity.class);
                startActivity(signInIntent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent = new Intent(getBaseContext(), SignUpActivity.class);
                startActivity(signupIntent);
            }
        });

        explainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explainSignupDialog();
            }
        });

    }

    private void explainSignupDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);

        // set the custom dialog components - text and button
        TextView headerText = (TextView) dialog.findViewById(R.id.dialog_header);
        headerText.setText("Glasgow Cycling");
        TextView contentText = (TextView) dialog.findViewById(R.id.dialog_content);
        contentText.setText("Currently the city has little reliable information on cycle usage. It's therefore difficult" +
                " to prioritise spend and provide infrastructure where it is truly needed. Each time a user records a route" +
                " with this app the city will get a clearer picture of the cycling community. With your help we can make Glasgow" +
                " a cycling city. \n\n To do this we need to collect route data, we would like to wrap this around a useful app" +
                " to help you record routes and track your usage, this requires a means of authentication. Email addresses and" +
                " passwords are stored securely and will never be made public.");

        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_close);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
