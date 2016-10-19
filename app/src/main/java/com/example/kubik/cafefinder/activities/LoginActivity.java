package com.example.kubik.cafefinder.activities;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kubik.cafefinder.R;
import com.google.android.gms.common.SignInButton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Class for working with login_activity
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.img_logo)
    Image mImgLogo;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        ButterKnife.bind(this);
        
    }
}
