package com.openlearning.ilearn.news.modals;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.firebase.firestore.ServerTimestamp;
import com.openlearning.ilearn.modals.PostReact;
import com.openlearning.ilearn.modals.StorageImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class News extends BaseObservable implements Parcelable, Serializable {

    public static final String PARCELABLE_KEY = "news_key";

    private String id;
    private String heading;
    private String title;
    private String body;
    private String instructorID;
    private boolean active;
    private List<StorageImage> storageImageList = new ArrayList<>();
    private List<PostReact> postReactList = new ArrayList<>();
    @ServerTimestamp
    private Date createdDate;

    public News() {
    }

    public News(String heading, String title, String body, String instructorID, boolean active) {
        this.id = UUID.randomUUID().toString();
        this.heading = heading;
        this.title = title;
        this.body = body;
        this.instructorID = instructorID;
        this.active = active;
    }

    public News(String heading, String title, String body, String instructorID, boolean active, List<StorageImage> storageImageList) {
        this(heading, title, body, instructorID, active);
        this.storageImageList = storageImageList;
    }


    protected News(Parcel in) {
        id = in.readString();
        heading = in.readString();
        title = in.readString();
        body = in.readString();
        instructorID = in.readString();
        active = in.readByte() != 0;
        storageImageList = in.createTypedArrayList(StorageImage.CREATOR);
        postReactList = in.createTypedArrayList(PostReact.CREATOR);
        createdDate = (Date) in.readSerializable();

    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public String getId() {
        return id;
    }

    @Bindable
    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Bindable
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(String instructorID) {
        this.instructorID = instructorID;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public List<StorageImage> getStorageImageList() {
        return storageImageList;
    }

    public void setStorageImageList(List<StorageImage> storageImageList) {
        this.storageImageList = storageImageList;
    }

    public List<PostReact> getPostReactList() {
        return postReactList;
    }

    public void setPostReactList(List<PostReact> postReactList) {
        this.postReactList = postReactList;
    }

    @Bindable
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(heading);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(instructorID);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeTypedList(storageImageList);
        dest.writeTypedList(postReactList);
        dest.writeSerializable(createdDate);
    }
}
