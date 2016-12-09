package com.example.kubik.cafefinder.database;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.kubik.cafefinder.database.models.CafeInfo;
import com.example.kubik.cafefinder.database.models.Profile;
import com.example.kubik.cafefinder.helpers.ImageConverter;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by Kubik on 12/9/16.
 */

public final class DbHelper {

    private static DbHelper instance = null;

    private Realm mRealm;

    private DbHelper(Context context) {
        Realm.init(context);
        this.mRealm = Realm.getDefaultInstance();
    }

    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }

    public Profile createProfile(String name, String email, String password) {
        mRealm.beginTransaction();
        final Profile profile = mRealm.createObject(Profile.class);
        profile.setName(name);
        profile.setEmail(email);
        profile.setPersonId(password);
        mRealm.commitTransaction();
        return profile;
    }

    public void updateProfilePhoto(Profile profile, Bitmap image) {
        mRealm.beginTransaction();
        profile.setPhoto(ImageConverter.bitmapToByteConverter(image));
        mRealm.commitTransaction();
    }

    public void addFavouriteCafe(Profile profile, CafeInfo cafe) {
        mRealm.beginTransaction();
        profile.getFavouriteList().add(cafe);
        mRealm.commitTransaction();
    }

    public Profile loginProfile(String email, String password) {
        return mRealm.where(Profile.class).equalTo("email", email)
                .equalTo("personId", password).findFirst();
    }

    public boolean isProfileExist(String email) {
        RealmQuery<Profile> query = mRealm.where(Profile.class).equalTo("email", email);
        return query.count() != 0;
    }

    public void close() {
        this.mRealm.close();
    }


}
