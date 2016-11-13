package com.example.kubik.cafefinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kubik.cafefinder.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Class for working with login_activity
 */

public class LoginActivity extends BaseCafeActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";


    @BindView(R.id.img_logo)
    ImageView mImgLogo;
    @BindView(R.id.et_email)
    EditText mEtEmail;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    @BindView(R.id.btn_google_login)
    SignInButton mBtnGoogleLogin;
    @BindView(R.id.tv_create_account)
    TextView mTvCreateAccount;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.login_activity);
        super.onCreate(savedInstanceState);

        Picasso.with(this).load(R.drawable.logo_cafe).into(mImgLogo);
    }

    @OnClick(R.id.btn_login)
    public void onBtnLoginClicked() {
        Log.d(TAG, "Button Login Clicked");
    }

    @OnClick(R.id.btn_google_login)
    public void onBtnGoogleLoginClicked() {
        Log.d(TAG, "Button Google Login Clicked");
        login();
    }

    private void login() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(BaseCafeActivity.sGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully
            GoogleSignInAccount acct = result.getSignInAccount();
            startMainActivity(acct);
        } else {
            // Signed out
            Toast.makeText(this, R.string.cant_login_msg, Toast.LENGTH_LONG).show();
        }
    }

    private void startMainActivity(GoogleSignInAccount acct) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseCafeActivity.sSignInAccount = acct;
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
        Log.d(TAG, "Error msg: " + connectionResult.getErrorMessage() + "\nError code: "
                + connectionResult.getErrorCode());
    }
}