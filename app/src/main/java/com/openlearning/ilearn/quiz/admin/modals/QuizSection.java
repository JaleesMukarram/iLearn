package com.openlearning.ilearn.quiz.admin.modals;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.UUID;

public class QuizSection implements Parcelable, Serializable {

    public static final Creator<QuizSection> CREATOR = new Creator<QuizSection>() {
        @Override
        public QuizSection createFromParcel(Parcel in) {
            return new QuizSection(in);
        }

        @Override
        public QuizSection[] newArray(int size) {
            return new QuizSection[size];
        }
    };

    public static final String PARCELABLE_KEY = "quiz_section_key";
    private String id;
    private String name;
    private String quizID;

    public QuizSection() {
    }

    public QuizSection(String name, String quizID) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.quizID = quizID;
    }

    protected QuizSection(Parcel in) {
        id = in.readString();
        name = in.readString();
        quizID = in.readString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuizID() {
        return quizID;
    }

    public void setQuizID(String quizID) {
        this.quizID = quizID;
    }


    @Override
    public String toString() {
        return "QuizSection{" +
                "ID='" + id + '\'' +
                ", name='" + name + '\'' +
                ", quizID='" + quizID + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(quizID);
    }


}
