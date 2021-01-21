package com.openlearning.ilearn.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.openlearning.ilearn.chat.queries.DocumentChat;

import java.io.Serializable;

@IgnoreExtraProperties
public class StorageDocument extends StorageItem implements Serializable, Parcelable {

    private long documentSize;
    private String documentLocalUri;

    public StorageDocument() {
    }


    public StorageDocument(String name, String downloadURL, String completePath) {
        super(name, downloadURL, completePath);
    }


    public StorageDocument(String name, String downloadURL, String completePath, long documentSize, String documentLocalUri) {
        super(name, downloadURL, completePath);
        this.documentSize = documentSize;
        this.documentLocalUri = documentLocalUri;
    }

    protected StorageDocument(Parcel in) {
        super(in);
        documentSize = in.readLong();
        documentLocalUri = in.readString();

    }

    public static final Creator<StorageDocument> CREATOR = new Creator<StorageDocument>() {
        @Override
        public StorageDocument createFromParcel(Parcel in) {
            return new StorageDocument(in);
        }

        @Override
        public StorageDocument[] newArray(int size) {
            return new StorageDocument[size];
        }
    };

    public long getDocumentSize() {
        return documentSize;
    }

    public void setDocumentSize(long documentSize) {
        this.documentSize = documentSize;
    }

    public void saveDatabaseValues(StorageDocument newDocument){

        super.setCompletePath(newDocument.getCompletePath());
        super.setDownloadURL(newDocument.getDownloadURL());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(documentSize);
        dest.writeString(documentLocalUri);
    }

    @Exclude
    public String getDocumentLocalUri() {
        return documentLocalUri;
    }

    public void setDocumentLocalUri(String documentLocalUri) {
        this.documentLocalUri = documentLocalUri;
    }
}
