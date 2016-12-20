package com.example.kubik.cafefinder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.database.models.Profile;
import com.example.kubik.cafefinder.dialogs.ChoosePictureDialog;
import com.example.kubik.cafefinder.helpers.ImageConverter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Activity to create new account
 * Created by Kubik on 12/9/16.
 */

public class CreateAccountActivity extends BaseCafeActivity implements ChoosePictureDialog.ChoosePictureDialogListener {

    private static int PASSWORD_LENGTH_FROM = 6;
    private static int PASSWORD_LENGTH_TO = 20;

    private static final int REQUEST_CODE_CAMERA = 101;
    private static final int REQUEST_CODE_GALLERY = 102;

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
        if (mToolbar != null) {
            getSupportActionBar().setTitle(R.string.create_account_title);
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
        if (mName.length() == 0) {
            Toast.makeText(this, R.string.enter_name_msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (sDbHelper.isProfileExist(mEmail)) {
            Toast.makeText(this, R.string.account_exist_msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mEmail.length() == 0) {
            Toast.makeText(this, R.string.no_email_msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        if ( !(mPassword.length() >= PASSWORD_LENGTH_FROM)) {
            Toast.makeText(this, R.string.short_password_msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        if ( !(mPassword.length() <= PASSWORD_LENGTH_TO)) {
            Toast.makeText(this, R.string.long_password_msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case REQUEST_CODE_CAMERA:
                if(resultCode == RESULT_OK){
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mImgCreateProfile.setImageBitmap(imageBitmap);
                }

                break;
            case REQUEST_CODE_GALLERY:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    mImgCreateProfile.setImageURI(selectedImage);
                }
                break;
        }
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

    @OnClick(R.id.img_create_profile)
    public void onChoosePictureClicked() {
        ChoosePictureDialog dialog = new ChoosePictureDialog();
        dialog.show(getSupportFragmentManager(), "ChoosePictureDialog");
    }

    @Override
    public void onMethodChosen(int code) {
        switch (code) {
            case 0:
                getPictureFromCamera();
                break;
            case 1:
                getPictureFromGallery();
                break;
            default:
                break;
        }
    }

    private void getPictureFromCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA);
        } else {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, REQUEST_CODE_CAMERA);
        }
    }

    private void getPictureFromGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY);
            } else {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , REQUEST_CODE_GALLERY);
            }
        } else {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , REQUEST_CODE_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, REQUEST_CODE_CAMERA);
                }
                break;
            }
            case REQUEST_CODE_GALLERY:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , REQUEST_CODE_GALLERY);
                }
                break;
            default:
                break;

        }
    }
}
