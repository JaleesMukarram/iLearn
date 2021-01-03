package com.openlearning.ilearn.quiz.admin.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.UUID;

@IgnoreExtraProperties
public class QuestionOption implements Serializable, Parcelable {

    public static final String PARCELABLE_KEY = "option_key";

    public static final Creator<QuestionOption> CREATOR = new Creator<QuestionOption>() {
        @Override
        public QuestionOption createFromParcel(Parcel in) {
            return new QuestionOption(in);
        }

        @Override
        public QuestionOption[] newArray(int size) {
            return new QuestionOption[size];
        }
    };

    private String id;
    private String title;
    private String questionID;
    private boolean correct;

    @Exclude
    private boolean selected;

    public QuestionOption() {
    }

    public QuestionOption(String title, String questionID, boolean correct) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.questionID = questionID;
        this.correct = correct;
    }

    protected QuestionOption(Parcel in) {
        id = in.readString();
        title = in.readString();
        questionID = in.readString();
        correct = in.readByte() != 0;
        selected = in.readByte() != 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(questionID);
        dest.writeByte((byte) (correct ? 1 : 0));
        dest.writeByte((byte) (selected ? 1 : 0));
    }
}
