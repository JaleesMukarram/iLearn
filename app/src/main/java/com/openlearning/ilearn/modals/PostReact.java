package com.openlearning.ilearn.modals;

import android.os.Parcel;
import android.os.Parcelable;

public class PostReact implements Parcelable {

    public static final int REACT_LIKE = 1;
    public static final int REACT_DISLIKE = 2;

    private String userID;
    private int likeStatus;

    public PostReact() {
    }

    public PostReact(String userID, int likeStatus) {
        this.userID = userID;
        this.likeStatus = likeStatus;
    }

    protected PostReact(Parcel in) {
        userID = in.readString();
        likeStatus = in.readInt();
    }

    public static final Creator<PostReact> CREATOR = new Creator<PostReact>() {
        @Override
        public PostReact createFromParcel(Parcel in) {
            return new PostReact(in);
        }

        @Override
        public PostReact[] newArray(int size) {
            return new PostReact[size];
        }
    };

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    @Override
    public String toString() {
        return "PostReact{" +
                "userID='" + userID + '\'' +
                ", likeStatus=" + likeStatus +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeInt(likeStatus);
    }
}
