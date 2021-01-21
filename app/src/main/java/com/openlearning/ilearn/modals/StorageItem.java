package com.openlearning.ilearn.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
@com.google.firebase.firestore.IgnoreExtraProperties
public class StorageItem implements Parcelable, Serializable {

    private String name;
    private String downloadURL;
    private String completePath;

    public StorageItem() {
    }

    public StorageItem(String name, String downloadURL, String completePath) {
        this.name = name;
        this.downloadURL = downloadURL;
        this.completePath = completePath;
    }

    protected StorageItem(Parcel in) {
        name = in.readString();
        downloadURL = in.readString();
        completePath = in.readString();
    }

    public static final Creator<StorageItem> CREATOR = new Creator<StorageItem>() {
        @Override
        public StorageItem createFromParcel(Parcel in) {
            return new StorageItem(in);
        }

        @Override
        public StorageItem[] newArray(int size) {
            return new StorageItem[size];
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
    @com.google.firebase.firestore.Exclude
    public String getStorageProcessingPath() {

        return this.completePath + "/" + this.name;
    }

    @Override
    public String toString() {
        return "StorageItem{" +
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
