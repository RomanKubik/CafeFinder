package com.example.kubik.cafefinder.database.models;

import io.realm.RealmObject;

/**
 * Created by Kubik on 12/8/16.
 */

public class Image extends RealmObject {
    private byte[] image;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
