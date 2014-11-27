package com.fcd.glasgowcycling.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fcd.glasgowcycling.CyclingApplication;
import com.fcd.glasgowcycling.LoadingView;
import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.api.auth.CyclingAuthenticator;
import com.fcd.glasgowcycling.api.http.ApiClientModule;
import com.fcd.glasgowcycling.api.http.GoCyclingApiInterface;
import com.fcd.glasgowcycling.api.responses.AuthModel;

import net.hockeyapp.android.UpdateManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class SignInActivity extends AccountAuthenticatorActivity {

    private static final String TAG = "SignInActivity";

    // Bundle Args
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "ADDING_NEW_ACCOUNT";
    public static final String PARAM_USER_PASS = "USER_PASSWORD";

    // Views
    @InjectView(R.id.email) EditText emailField;
    @InjectView(R.id.password) EditText passwordField;

    @InjectView(R.id.sign_in_button) Button signInButton;
    @InjectView(R.id.loading_view) LoadingView loadingView;

    // API
    GoCyclingApiInterface sCyclingService;
    AccountManager mAccountManager;
    private boolean fromAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.inject(this);
        sCyclingService = new ApiClientModule(this, (CyclingApplication)getApplication()).provideAuthClient();
        fromAccountManager = getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false);
        Log.d(TAG, "From account manager: " + (fromAccountManager ? "YES" : "NO"));

        mAccountManager = AccountManager.get(this);

        passwordField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    signInButton.setEnabled(false);
                    loadingView.startAnimating();
                    new LoginTask().execute();
                    return true;
                } else {
                    return false;
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign In clicked");
                signInButton.setEnabled(false);
                loadingView.startAnimating();
                new LoginTask().execute();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateManager.register(this, "31f27fc9f4a5e74f41ed1bfe0ab10860");
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
        if (id == R.id.action_forgotten_password) {
            startActivity(new Intent(getApplicationContext(), AccountForgottenActivity.class));
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
        boolean accountExists = mAccountManager.getAccountsByType(CyclingAuthenticator.ACCOUNT_TYPE).length > 0;
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
            Intent userIntent = new Intent(getBaseContext(), UserOverviewActivity.class);
            userIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(userIntent);
        }

        finish();
    }

    public void showToast(final String message, final int length) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(SignInActivity.this, message, length).show();
            }
        });
    }

    public void loginFailed() {
        runOnUiThread(new Runnable() {
            public void run() {
                signInButton.setEnabled(true);
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

    private class LoginTask extends AsyncTask<Void, Void, Intent> {
        @Override
        protected Intent doInBackground(Void... params) {
            // Dismiss keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            if(imm.isAcceptingText()) { // verify if the soft keyboard is open
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            // Sign in
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            AuthModel authModel = null;
            try {
                authModel = sCyclingService.signin(email, password);
            } catch (RetrofitError error) {
                Log.d(TAG, "Retrofit error");
                if (error.isNetworkError()) {
                    Log.d(TAG, "Network error");
                    showToast("Check your connection and try again", Toast.LENGTH_SHORT);
                } else if (error.getResponse().getStatus() == 401) {
                    // Unauthorized
                    Log.d(TAG, "Invalid details");
                    showToast("The details you entered were incorrect", Toast.LENGTH_LONG);
                } else {
                    showToast("Login failed, try again", Toast.LENGTH_SHORT);
                }
                loginFailed();
                return null;
            }
            if (authModel != null) {
                String authToken = authModel.getUserToken();
                String refreshToken = authModel.getRefreshToken();
                final Intent res = new Intent();
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, email);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, CyclingAuthenticator.ACCOUNT_TYPE);
                res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                res.putExtra(CyclingAuthenticator.KEY_REFRESH_TOKEN, refreshToken);
                res.putExtra(PARAM_USER_PASS, password);
                return res;
            } else {
                Log.d(TAG, "Login failed");
                showToast("Failed to login", Toast.LENGTH_SHORT);
                loginFailed();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Intent intent) {
            if (intent != null) {
                finishLogin(intent);
            }
        }
    }
}
