package com.openlearning.ilearn.quiz.modals;

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
    @ServerTimestamp
    private Date createdDate;
    private boolean active;

    public Subject() {
    }

    public Subject(String name, String description, boolean active) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public Subject(String id, String name, String description, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    protected Subject(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
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

    public void setId(String id) {
        this.id = id;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", active=" + active +
                '}';
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
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeSerializable(createdDate);
    }
}
