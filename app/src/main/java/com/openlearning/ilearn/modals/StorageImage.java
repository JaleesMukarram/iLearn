package com.openlearning.ilearn.modals;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class StorageImage extends StorageItem implements Parcelable, Serializable {


    public StorageImage() {
    }

    public StorageImage(Parcel in) {
        super(in);
    }

    public StorageImage(String name, String downloadURL, String completePath) {
        super(name, downloadURL, completePath);
    }

    public static final Creator<StorageImage> CREATOR = new Creator<StorageImage>() {
        @Override
        public StorageImage createFromParcel(Parcel in) {
            return new StorageImage(in);
        }

        @Override
        public StorageImage[] newArray(int size) {
            return new StorageImage[size];
        }
    };

    @Override
    public String toString() {
        return super.toString();
    }
}
