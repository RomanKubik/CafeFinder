package com.example.kubik.cafefinder.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.database.models.Profile;
import com.example.kubik.cafefinder.helpers.ImageConverter;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Activity to create new account
 * Created by Kubik on 12/9/16.
 */

public class CreateAccountActivity extends BaseCafeActivity {

    private static int PASSWORD_LENGTH_FROM = 6;
    private static int PASSWORD_LENGTH_TO = 20;

    @BindView(R.id.tb_create_account_activity)
    Toolbar mToolbar;
    @BindView(R.id.img_create_profile)
    ImageView mImgCreateProfile;
    @BindView(R.id.et_create_name)
    EditText mEtName;
    @BindView(R.id.et_create_email)
    EditText mEtEmail;
    @BindView(R.id.et_create_password)
    EditText mEtPassword;
    @BindView(R.id.btn_create_account)
    Button mBtnCreate;

    private String mName;
    private String mEmail;
    private String mPassword;
    private Bitmap mImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.create_account_activity);
        super.onCreate(savedInstanceState);

        setToolbar();
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.create_account_title);
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    private boolean isValid() {
        if ( !(mPassword.length() >= PASSWORD_LENGTH_FROM)) {
            Toast.makeText(this, R.string.short_password_msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        if ( !(mPassword.length() <= PASSWORD_LENGTH_TO)) {
            Toast.makeText(this, R.string.long_password_msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (sDbHelper.isProfileExist(mEmail)) {
            Toast.makeText(this, R.string.account_exist_msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mName.length() == 0) {
            Toast.makeText(this, R.string.enter_name_msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.btn_create_account)
    public void onCreateAccountClicked() {
        mName = mEtName.getText().toString();
        mEmail = mEtEmail.getText().toString();
        mPassword = mEtPassword.getText().toString();
        mImage = ImageConverter.drawableToBitmap(mImgCreateProfile.getDrawable());
        if (isValid()) {
            Profile profile = sDbHelper.createProfile(mName, mEmail, mPassword);
            sDbHelper.updateProfilePhoto(profile, mImage);
            Toast.makeText(this, R.string.success_msg, Toast.LENGTH_SHORT).show();
            finish();
        }
    }


}
