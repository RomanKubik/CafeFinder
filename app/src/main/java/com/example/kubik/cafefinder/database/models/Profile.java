package com.example.kubik.cafefinder.database.models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Kubik on 12/8/16.
 */

public class Profile extends RealmObject {
    private String name;
    private String email;
    private String personId;
    private byte[] photo;
    private RealmList<CafeInfo> favouriteList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public RealmList<CafeInfo> getFavouriteList() {
        return favouriteList;
    }

    public void setFavouriteList(RealmList<CafeInfo> favouriteList) {
        this.favouriteList = favouriteList;
    }
}
