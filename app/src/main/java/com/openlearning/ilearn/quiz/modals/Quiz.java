package com.openlearning.ilearn.quiz.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Quiz implements Parcelable, Serializable {

    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };

    public static final String PARCELABLE_KEY = "quiz_key";
    private String quizID;
    private String quizName;
    private String quizDescription;
    private int quizDurationMin;
    private String quizSubjectID;
    private int passedTimedSeconds;

    public Quiz() {
    }

    public Quiz(String quizName, String quizDescription, int quizDurationMin, String quizSubjectID) {
        this.quizID = UUID.randomUUID().toString();
        this.quizName = quizName;
        this.quizDescription = quizDescription;
        this.quizDurationMin = quizDurationMin;
        this.quizSubjectID = quizSubjectID;
    }

    protected Quiz(Parcel in) {
        quizID = in.readString();
        quizName = in.readString();
        quizDescription = in.readString();
        quizDurationMin = in.readInt();
        quizSubjectID = in.readString();
    }

    public String getQuizID() {
        return quizID;
    }

    public void setQuizID(String quizID) {
        this.quizID = quizID;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getQuizDescription() {
        return quizDescription;
    }

    public void setQuizDescription(String quizDescription) {
        this.quizDescription = quizDescription;
    }

    public int getQuizDurationMin() {
        return quizDurationMin;
    }

    public void setQuizDurationMin(int quizDurationMin) {
        this.quizDurationMin = quizDurationMin;
    }

    public String getQuizSubjectID() {
        return quizSubjectID;
    }

    public void setQuizSubjectID(String quizSubjectID) {
        this.quizSubjectID = quizSubjectID;
    }

    public int getPassedTimedSeconds() {
        return passedTimedSeconds;
    }

    public void setPassedTimedSeconds(int passedTimedSeconds) {
        this.passedTimedSeconds = passedTimedSeconds;
    }

    public void incrementQuizPassedTimesSeconds() {

        this.passedTimedSeconds++;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "quizID=" + quizID +
                ", quizName='" + quizName + '\'' +
                ", quizDescription='" + quizDescription + '\'' +
                ", quizDurationMin=" + quizDurationMin +
                ", quizSubjectID=" + quizSubjectID +
                ", passedTimedSeconds=" + passedTimedSeconds +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(quizID);
        parcel.writeString(quizName);
        parcel.writeString(quizDescription);
        parcel.writeInt(quizDurationMin);
        parcel.writeString(quizSubjectID);
    }
}
