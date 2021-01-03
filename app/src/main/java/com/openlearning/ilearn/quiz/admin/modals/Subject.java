package com.openlearning.ilearn.quiz.admin.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.UUID;

public class Subject implements Parcelable {

    public static final String PARCELABLE_KEY = "subject_key";
    private String id;
    private String name;
    private String description;
    private String instructorID;
    private boolean active;
    @ServerTimestamp
    private Date createdDate;

    public Subject() {
    }

    public Subject(String name, String description, String instructorID, boolean active) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.instructorID = instructorID;
        this.active = active;
    }

    protected Subject(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        instructorID = in.readString();
        active = in.readByte() != 0;
        createdDate = (Date) in.readSerializable();
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(String instructorID) {
        this.instructorID = instructorID;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(instructorID);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeSerializable(createdDate);
    }
}
