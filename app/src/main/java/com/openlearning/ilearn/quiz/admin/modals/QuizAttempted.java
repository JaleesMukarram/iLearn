package com.openlearning.ilearn.quiz.admin.modals;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class QuizAttempted {

    private String userID;
    private String quizID;
    private double obtainedMarks;

    @ServerTimestamp
    private Date attemptDate;

    public QuizAttempted() {
    }

    public QuizAttempted(String userID, String quizID, double obtainedMarks) {
        this.userID = userID;
        this.quizID = quizID;
        this.obtainedMarks = obtainedMarks;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getQuizID() {
        return quizID;
    }

    public void setQuizID(String quizID) {
        this.quizID = quizID;
    }

    public double getObtainedMarks() {
        return obtainedMarks;
    }

    public void setObtainedMarks(double obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
    }

    public Date getAttemptDate() {
        return attemptDate;
    }

    public void setAttemptDate(Date attemptDate) {
        this.attemptDate = attemptDate;
    }

    @Override
    public String toString() {
        return "QuizAttempted{" +
                "userID='" + userID + '\'' +
                ", quizID='" + quizID + '\'' +
                ", obtainedMarks=" + obtainedMarks +
                ", attemptDate=" + attemptDate +
                '}';
    }
}
