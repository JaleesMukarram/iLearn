package com.openlearning.ilearn.quiz.admin.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class QuizQuestion implements Parcelable, Serializable {


    public static final Creator<QuizQuestion> CREATOR = new Creator<QuizQuestion>() {
        @Override
        public QuizQuestion createFromParcel(Parcel in) {
            return new QuizQuestion(in);
        }

        @Override
        public QuizQuestion[] newArray(int size) {
            return new QuizQuestion[size];
        }
    };
    private String id;
    private String name;
    private double positive;
    private double negative;
    // Tracking variables
    private boolean visited;
    private boolean answered;
    private boolean marked;

    private List<QuestionOption> questionOptionList;

    public QuizQuestion() {
    }

    public QuizQuestion(String id, String name, double positive, double negative, List<QuestionOption> questionOptionList) {
        this.id = id;
        this.name = name;
        this.positive = positive;
        this.negative = negative;
        this.questionOptionList = questionOptionList;
    }

    protected QuizQuestion(Parcel in) {
        id = in.readString();
        name = in.readString();
        positive = in.readDouble();
        negative = in.readDouble();
        visited = in.readByte() != 0;
        answered = in.readByte() != 0;
        marked = in.readByte() != 0;
        questionOptionList = in.createTypedArrayList(QuestionOption.CREATOR);
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPositive() {
        return positive;
    }

    public void setPositive(double positive) {
        this.positive = positive;
    }

    public double getNegative() {
        return negative;
    }

    public void setNegative(double negative) {
        this.negative = negative;
    }


    public List<QuestionOption> getQuestionOptionList() {
        return questionOptionList;
    }

    public void setQuestionOptionList(List<QuestionOption> questionOptionList) {
        this.questionOptionList = questionOptionList;
    }

    @Exclude
    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    @Exclude
    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    @Exclude
    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    @Override
    public String toString() {
        return "QuizQuestion{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", positive=" + positive +
                ", negative=" + negative +
                ", visited=" + visited +
                ", answered=" + answered +
                ", marked=" + marked +
                ", questionOptionList=" + questionOptionList +
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
        parcel.writeDouble(positive);
        parcel.writeDouble(negative);
        parcel.writeByte((byte) (visited ? 1 : 0));
        parcel.writeByte((byte) (answered ? 1 : 0));
        parcel.writeByte((byte) (marked ? 1 : 0));
        parcel.writeTypedList(questionOptionList);
    }
}
