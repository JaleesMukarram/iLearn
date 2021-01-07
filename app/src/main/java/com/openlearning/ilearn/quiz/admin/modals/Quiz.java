package com.openlearning.ilearn.quiz.admin.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;


@IgnoreExtraProperties
public class Quiz implements Parcelable, Serializable {

    public static final String PARCELABLE_KEY = "quiz_key";
    private String quizID;
    private String quizName;
    private String quizDescription;
    private int quizDurationMin;
    private String quizSubjectID;
    private boolean active;
    private int passedTimedSeconds;
    private List<QuizSection> quizSectionList;

    private boolean practice;

    public Quiz() {
    }

    public Quiz(String quizName, String quizDescription, int quizDurationMin, String quizSubjectID, boolean active, boolean practice) {
        this.quizID = UUID.randomUUID().toString();
        this.quizName = quizName;
        this.quizDescription = quizDescription;
        this.quizDurationMin = quizDurationMin;
        this.quizSubjectID = quizSubjectID;
        this.active = active;
        this.practice = practice;
    }

    protected Quiz(Parcel in) {
        quizID = in.readString();
        quizName = in.readString();
        quizDescription = in.readString();
        quizDurationMin = in.readInt();
        quizSubjectID = in.readString();
        active = in.readByte() != 0;
        passedTimedSeconds = in.readInt();
        quizSectionList = in.createTypedArrayList(QuizSection.CREATOR);
        practice = in.readByte() != 0;
    }

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

    public String getQuizID() {
        return quizID;
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

    public boolean isPractice() {
        return practice;
    }

    public void setPractice(boolean practice) {
        this.practice = practice;
    }

    @Exclude
    public int getPassedTimedSeconds() {
        return passedTimedSeconds;
    }

    public void setPassedTimedSeconds(int passedTimedSeconds) {
        this.passedTimedSeconds = passedTimedSeconds;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Helping methods
    public void incrementQuizPassedTimesSeconds() {

        this.passedTimedSeconds++;
    }

    @Exclude
    public double getTotalMarks() {

        if (quizSectionList == null) {
            return 0;
        }

        double marks = 0;

        for (QuizSection section : quizSectionList) {

            marks += section.getTotalMarks();
        }

        return marks;
    }

    @Exclude
    public int getTotalQuestions() {

        if (quizSectionList == null) {
            return 0;
        }

        int questions = 0;

        for (QuizSection section : quizSectionList) {

            questions += section.getTotalQuestions();
        }

        return questions;
    }

    @Exclude
    public List<QuizSection> getQuizSectionList() {
        return quizSectionList;
    }

    public void setQuizSectionList(List<QuizSection> quizSectionList) {
        this.quizSectionList = quizSectionList;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "quizID='" + quizID + '\'' +
                ", quizName='" + quizName + '\'' +
                ", quizDescription='" + quizDescription + '\'' +
                ", quizDurationMin=" + quizDurationMin +
                ", quizSubjectID='" + quizSubjectID + '\'' +
                ", active=" + active +
                ", passedTimedSeconds=" + passedTimedSeconds +
                ", quizSectionList=" + quizSectionList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(quizID);
        dest.writeString(quizName);
        dest.writeString(quizDescription);
        dest.writeInt(quizDurationMin);
        dest.writeString(quizSubjectID);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeInt(passedTimedSeconds);
        dest.writeTypedList(quizSectionList);
        dest.writeByte((byte) (practice ? 1 : 0));

    }
}
