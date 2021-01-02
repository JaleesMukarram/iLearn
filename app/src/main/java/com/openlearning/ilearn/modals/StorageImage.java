package com.openlearning.ilearn.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class StorageImage implements Parcelable, Serializable {

    private String name;
    private String downloadURL;
    private String completePath;

    public StorageImage() {
    }

    public StorageImage(String name, String downloadURL, String completePath) {
        this.name = name;
        this.downloadURL = downloadURL;
        this.completePath = completePath;
    }

    protected StorageImage(Parcel in) {
        name = in.readString();
        downloadURL = in.readString();
        completePath = in.readString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getCompletePath() {
        return completePath;
    }

    public void setCompletePath(String completePath) {
        this.completePath = completePath;
    }

    @Exclude
    public String getStorageDeletePath() {

        return this.completePath + "/" + this.name;
    }

    @Override
    public String toString() {
        return "StorageImage{" +
                "name='" + name + '\'' +
                ", downloadURL='" + downloadURL + '\'' +
                ", completePath='" + completePath + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(downloadURL);
        dest.writeString(completePath);
    }
}
