package com.fcd.glasgowcycling.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.AuthModel;
import com.fcd.glasgowcycling.api.auth.CyclingAuthenticator;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class SignInActivity extends AccountAuthenticatorActivity {

    private static final String TAG = "SignInActivity";

    // Bundle Args
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "ADDING_NEW_ACCOUNT";
    public static final String PARAM_USER_PASS = "USER_PASSWORD";
    public final String ACCOUNT_TYPE = "com.fcd.GlasgowCycling";

    // Views
    @InjectView(R.id.email) AutoCompleteTextView emailField;
    @InjectView(R.id.password) EditText passwordField;

    @InjectView(R.id.sign_in_button) Button signInButton;
    @InjectView(R.id.sign_up_button) Button signupButton;
    @InjectView(R.id.signin_image) ImageView signinImageView;

    // API
    @Inject GoCyclingApiInterface sCyclingService;
    AccountManager mAccountManager;
    private boolean fromAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.inject(this);
        ((CyclingApplication) getApplication()).inject(this);

        fromAccountManager = getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false);
        Log.d(TAG, "From account manager: " + (fromAccountManager ? "YES" : "NO"));

        emailField.setText("chris.sloey@gmail.com");
        passwordField.setText("password");

        mAccountManager = AccountManager.get(this);

        signInButton.setOnClickListener(new SignInListener(sCyclingService));
        //signUpButton.setOnClickListener(new SignUpListener()); TODO signup
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

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        String refreshToken = intent.getStringExtra(CyclingAuthenticator.KEY_REFRESH_TOKEN);
        boolean addingAccount = getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false);
        boolean accountExists = mAccountManager.getAccountsByType(ACCOUNT_TYPE).length > 0;
        if (addingAccount || !accountExists) {
            String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, AccountManager.KEY_AUTHTOKEN, authToken);
            mAccountManager.setAuthToken(account, CyclingAuthenticator.KEY_REFRESH_TOKEN, refreshToken);
        } else {
            mAccountManager.setPassword(account, accountPassword);
            mAccountManager.setAuthToken(account, CyclingAuthenticator.KEY_REFRESH_TOKEN, refreshToken);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);

        if (!fromAccountManager) {
            // Load user overview
            startActivity(new Intent(this, UserOverviewActivity.class));
        }

        finish();
    }

    private class SignInListener implements View.OnClickListener {

        private GoCyclingApiInterface cyclingService;

        public SignInListener(GoCyclingApiInterface cyclingService) {
            this.cyclingService = cyclingService;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Sign In clicked");
            final String email = emailField.getText().toString();
            final String password = passwordField.getText().toString();
            new AsyncTask<Void, Void, Intent>() {
                @Override
                protected Intent doInBackground(Void... params) {
                    AuthModel authModel = null;
                    try {
                        authModel = cyclingService.signin(email, password);
                    } catch (RetrofitError error) {
                        Log.d(TAG, "Retrofit error");
                        if (error.isNetworkError()) {
                            Log.d(TAG, "Network error");
                        } else if (error.getResponse().getStatus() == 401) {
                            // Unauthorized
                            Log.d(TAG, "Invalid details");
                        }
                        return null;
                    }
                    if (authModel != null) {
                        String authToken = authModel.getUserToken();
                        String refreshToken = authModel.getRefreshToken();
                        final Intent res = new Intent();
                        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, email);
                        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                        res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                        res.putExtra(CyclingAuthenticator.KEY_REFRESH_TOKEN, refreshToken);
                        res.putExtra(PARAM_USER_PASS, password);
                        return res;
                    } else {
                        Log.d(TAG, "Login failed");
                        return null;
                    }
                }
                @Override
                protected void onPostExecute(Intent intent) {
                    if (intent != null) {
                        finishLogin(intent);
                    }
                }
            }.execute();
        }
    }
}
