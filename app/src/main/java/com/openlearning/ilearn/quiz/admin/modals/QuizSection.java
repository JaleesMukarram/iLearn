package com.openlearning.ilearn.quiz.admin.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class QuizSection implements Parcelable, Serializable {


    public static final String PARCELABLE_KEY = "quiz_section_key";
    private String id;
    private String name;
    private String quizID;
    private List<QuizQuestion> questionList;


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
        questionList = in.createTypedArrayList(QuizQuestion.CREATOR);
    }

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

    @Exclude
    public List<QuizQuestion> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<QuizQuestion> questionList) {
        this.questionList = questionList;
    }

    // Helping methods
    @Exclude
    public double getTotalMarks() {

        if (questionList == null) return 0;

        double marks = 0;

        for (QuizQuestion question : questionList) {

            marks += question.getPositive();
        }

        return marks;
    }

    @Exclude
    int getTotalQuestions() {

        if (questionList == null) return 0;

        return questionList.size();

    }

    @Override
    public String toString() {
        return "QuizSection{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", quizID='" + quizID + '\'' +
                ", questionList=" + questionList +
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
        dest.writeString(quizID);
        dest.writeTypedList(questionList);
    }
}
