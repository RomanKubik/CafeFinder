package com.example.kubik.cafefinder.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.kubik.cafefinder.database.models.DbCafeInfo;
import com.example.kubik.cafefinder.database.models.Image;
import com.example.kubik.cafefinder.database.models.Profile;
import com.example.kubik.cafefinder.database.models.Review;
import com.example.kubik.cafefinder.helpers.ImageConverter;
import com.example.kubik.cafefinder.models.CafeReview;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Helps to work with realm
 * Created by Kubik on 12/9/16.
 */

public final class DbHelper {

    private static DbHelper instance = null;

    private Realm mRealm;

    private DbHelper(Realm realm) {
        mRealm = realm;
    }

    public static DbHelper getInstance(Realm realm) {
        if (instance == null) {
            instance = new DbHelper(realm);
        }
        return instance;
    }

    /**
     *Working with profile
     */
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

    public Profile loginProfile(String email, String password) {
        return mRealm.where(Profile.class).equalTo("email", email)
                .equalTo("personId", password).findFirst();
    }

    public boolean isProfileExist(String email) {
        RealmQuery<Profile> query = mRealm.where(Profile.class).equalTo("email", email);
        return query.count() != 0;
    }

    public boolean isGoogleProfileExist(String personId) {
        RealmQuery<Profile> query = mRealm.where(Profile.class).equalTo("personId", personId);
        return query.count() != 0;
    }

    /**
     * Working with place cache
     */
    public boolean isFavouritePlace(Profile profile, String placeId) {
        RealmList<DbCafeInfo> cafeList = profile.getFavouriteList();
        RealmResults<DbCafeInfo> results = cafeList.where().equalTo("placeId", placeId).findAll();
        return results.size() != 0;
    }

    public void addFavouriteCafe(Profile profile, DbCafeInfo cafe) {
        mRealm.beginTransaction();
        profile.getFavouriteList().add(cafe);
        mRealm.commitTransaction();
    }

    public void addFavouriteCafe(Profile profile, String placeId, Place cafeDetails
            , List<Bitmap> images, List<CafeReview> reviews) {
        mRealm.beginTransaction();
        final DbCafeInfo dbCafeInfo = createCafeInfo(placeId, cafeDetails);
        addImageList(dbCafeInfo, images);
        addReviewList(dbCafeInfo, reviews);
        profile.getFavouriteList().add(dbCafeInfo);
        mRealm.commitTransaction();
    }

    public void removeFavouriteCafe(Profile profile, String placeId) {
        mRealm.beginTransaction();
        DbCafeInfo cafe = profile.getFavouriteList().where()
                .equalTo("placeId", placeId).findFirst();
        profile.getFavouriteList().remove(cafe);
        mRealm.commitTransaction();
    }

    public DbCafeInfo createCafeInfo(String placeId, String name, String address, String phoneNumber
            , String link, float rating, double lat, double lng, RealmList<Review> reviews
            , RealmList<Image> images) {
        mRealm.beginTransaction();
        final DbCafeInfo dbCafeInfo = mRealm.createObject(DbCafeInfo.class);
        dbCafeInfo.setPlaceId(placeId);
        dbCafeInfo.setName(name);
        dbCafeInfo.setAddress(address);
        dbCafeInfo.setPhoneNumber(phoneNumber);
        dbCafeInfo.setLink(link);
        dbCafeInfo.setRating(rating);
        dbCafeInfo.setLat(lat);
        dbCafeInfo.setLat(lng);
        dbCafeInfo.setReviews(reviews);
        dbCafeInfo.setImages(images);
        mRealm.commitTransaction();
        return dbCafeInfo;
    }

    public DbCafeInfo createCafeInfo(String placeId, Place cafeDetails) {
        boolean wasInTransaction = true;
        if (!mRealm.isInTransaction()) {
            mRealm.beginTransaction();
            wasInTransaction = false;
        }
        final  DbCafeInfo dbCafeInfo = mRealm.createObject(DbCafeInfo.class);
        dbCafeInfo.setPlaceId(placeId);
        dbCafeInfo.setName(cafeDetails.getName().toString());
        dbCafeInfo.setAddress(cafeDetails.getAddress().toString());
        dbCafeInfo.setRating(cafeDetails.getRating());
        dbCafeInfo.setLink(cafeDetails.getWebsiteUri().toString());
        dbCafeInfo.setPhoneNumber(cafeDetails.getPhoneNumber().toString());
        dbCafeInfo.setLat(cafeDetails.getLatLng().latitude);
        dbCafeInfo.setLng(cafeDetails.getLatLng().longitude);
        if (!wasInTransaction) {
            mRealm.commitTransaction();
        }
        return dbCafeInfo;
    }

    public Review createReview(String reviewName, String review, double rating) {
        boolean wasInTransaction = true;
        if (!mRealm.isInTransaction()) {
            mRealm.beginTransaction();
            wasInTransaction = false;
        }
        final Review reviewObj = mRealm.createObject(Review.class);
        reviewObj.setRating(rating);
        reviewObj.setReview(review);
        reviewObj.setReviewerName(reviewName);
        if (!wasInTransaction) {
            mRealm.commitTransaction();
        }
        return reviewObj;
    }

    public Image createImage(Bitmap image) {

        boolean wasInTransaction = true;
        if (!mRealm.isInTransaction()) {
            mRealm.beginTransaction();
            wasInTransaction = false;
        }
        final Image imageObj = mRealm.createObject(Image.class);
        imageObj.setImage(ImageConverter.bitmapToByteConverter(image));
        if (!wasInTransaction) {
            mRealm.commitTransaction();
        }
        return imageObj;
    }

    public RealmList<DbCafeInfo> getFavouriteList(Profile profile) {
        return profile.getFavouriteList();
    }

    public void addReview(DbCafeInfo dbCafeInfo, Review review) {
        mRealm.beginTransaction();
        dbCafeInfo.getReviews().add(review);
        mRealm.commitTransaction();
    }

    public void removeReview(DbCafeInfo dbCafeInfo, Review review) {
        mRealm.beginTransaction();
        dbCafeInfo.getReviews().remove(review);
        mRealm.commitTransaction();
    }

    public void addReviewList(DbCafeInfo dbCafeInfo, List<CafeReview> cafeReviews) {
        boolean wasInTransaction = true;
        if (!mRealm.isInTransaction()) {
            mRealm.beginTransaction();
            wasInTransaction = false;
        }
        for (CafeReview cafeReview : cafeReviews) {
            final Review review = createReview(cafeReview.getAuthorName(), cafeReview.getText()
                    , cafeReview.getRating());
            dbCafeInfo.getReviews().add(review);
        }
        if (!wasInTransaction) {
            mRealm.commitTransaction();
        }    }

    public RealmList<Review> getReviewList(DbCafeInfo dbCafeInfo) {
        return dbCafeInfo.getReviews();
    }

    public void addImage(DbCafeInfo dbCafeInfo, Image image) {
        mRealm.beginTransaction();
        dbCafeInfo.getImages().add(image);
        mRealm.commitTransaction();
    }

    public void removeImage(DbCafeInfo dbCafeInfo, Image image) {
        mRealm.beginTransaction();
        dbCafeInfo.getImages().remove(image);
        mRealm.commitTransaction();
    }

    public void addImageList(DbCafeInfo dbCafeInfo, List<Bitmap> images) {
        boolean wasInTransaction = true;
        if (!mRealm.isInTransaction()) {
            mRealm.beginTransaction();
            wasInTransaction = false;
        }
        for (Bitmap btm : images) {
            final Image image = createImage(btm);
            dbCafeInfo.getImages().add(image);
        }
        if (!wasInTransaction) {
            mRealm.commitTransaction();
        }
    }

    public RealmList<Image> getImageList(DbCafeInfo dbCafeInfo) {
        return dbCafeInfo.getImages();
    }

    public DbCafeInfo getCafeInfo(Profile profile, String placeId) {
        return profile.getFavouriteList().where().equalTo("placeId", placeId).findFirst();
    }

    public Review getReview(DbCafeInfo dbCafeInfo, int location) {
        return  dbCafeInfo.getReviews().get(location);
    }

    public Image getImage(DbCafeInfo dbCafeInfo, int location) {
        return dbCafeInfo.getImages().get(location);
    }

    public Place getPlaceInfo(Profile profile, final String placeId) {
        final DbCafeInfo dbCafeInfo = getCafeInfo(profile, placeId);

        return new Place() {
            @Override
            public Place freeze() {
                return null;
            }

            @Override
            public boolean isDataValid() {
                return true;
            }

            @Override
            public String getId() {
                return placeId;
            }

            @Override
            public List<Integer> getPlaceTypes() {
                return null;
            }

            @Override
            public CharSequence getAddress() {
                return dbCafeInfo.getAddress();
            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public CharSequence getName() {
                return dbCafeInfo.getName();
            }

            @Override
            public LatLng getLatLng() {
                LatLng latLng = new LatLng(dbCafeInfo.getLat(), dbCafeInfo.getLng());
                return latLng;
            }

            @Override
            public LatLngBounds getViewport() {
                return null;
            }

            @Override
            public Uri getWebsiteUri() {
                return Uri.parse(dbCafeInfo.getLink());
            }

            @Override
            public CharSequence getPhoneNumber() {
                return dbCafeInfo.getPhoneNumber();
            }

            @Override
            public float getRating() {
                return dbCafeInfo.getRating();
            }

            @Override
            public int getPriceLevel() {
                return 0;
            }

            @Override
            public CharSequence getAttributions() {
                return null;
            }
        };
    }

    public List<Bitmap> getImages(Profile profile, String placeId) {
        List<Bitmap> images = new ArrayList<>();
        for (Image img: getImageList(getCafeInfo(profile, placeId))) {
            images.add(ImageConverter.byteToBitmapConverter(img.getImage()));
        }
        return images;
    }

    public List<CafeReview> getReviews(Profile profile, String placeId) {
        List<CafeReview> reviews = new ArrayList<>();
        for (Review review : getReviewList(getCafeInfo(profile, placeId))) {
            CafeReview cafeReview = new CafeReview();
            cafeReview.setAuthorName(review.getReviewerName());
            cafeReview.setRating(review.getRating());
            cafeReview.setText(review.getReview());
            reviews.add(cafeReview);
        }
        return reviews;
    }

}
