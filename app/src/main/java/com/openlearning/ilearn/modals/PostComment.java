package com.openlearning.ilearn.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostComment implements Parcelable {

    private String userID;
    private String comment;
    private Date createdDate;

    public PostComment() {
    }

    public PostComment(String userID, String comment) {
        this.userID = userID;
        this.comment = comment;
        this.createdDate = new Date();
    }

    protected PostComment(Parcel in) {
        userID = in.readString();
        comment = in.readString();
        createdDate = (Date) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(comment);
        dest.writeSerializable(createdDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostComment> CREATOR = new Creator<PostComment>() {
        @Override
        public PostComment createFromParcel(Parcel in) {
            return new PostComment(in);
        }

        @Override
        public PostComment[] newArray(int size) {
            return new PostComment[size];
        }
    };

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "PostComment{" +
                "userID='" + userID + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
