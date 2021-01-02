package com.openlearning.ilearn.quiz.view_models;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.quiz.modals.Quiz;
import com.openlearning.ilearn.quiz.modals.QuizSection;
import com.openlearning.ilearn.quiz.repositories.QuizRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class QuizSectionsVM extends ViewModel {

    private final QuizRepository quizRepository;
    private String subjectID;
    private Quiz quiz;
    private final MutableLiveData<List<QuizSection>> quizSections;

    public QuizSectionsVM() {

        quizRepository = QuizRepository.getInstance();
        quizSections = new MutableLiveData<>();

    }

    public void addNewsQuizSection(Activity activity, String sectionName) {

        if (!sectionName.equals("") && sectionName.length() > 0) {

            QuizSection quizSection = new QuizSection(sectionName, quiz.getQuizID());

            quizRepository.insertNewQuizSection(quizSection, quiz, new FirebaseSuccessListener() {
                @Override
                public void onSuccess(Object obj) {

                    CommonUtils.showSuccessDialogue(activity, "Quiz Section Added");
                }

                @Override
                public void onFailure(Exception ex) {

                    CommonUtils.showWarningDialogue(activity, ex.getLocalizedMessage());

                }
            });

        } else {

            CommonUtils.showWarningDialogue(activity, "Please enter valid section name");
        }
    }

    public void initIDs(Quiz quiz) {

        this.quiz = quiz;
    }

    public void getAllSections(Activity activity) {

        quizRepository.getQuizSectionFromDatabase(true, quiz.getQuizSubjectID(), quiz.getQuizID(), new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                quizSections.setValue((List<QuizSection>) obj);

            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showWarningDialogue(activity, ex.getLocalizedMessage());

            }
        });
    }

    public MutableLiveData<List<QuizSection>> getQuizSections() {
        return quizSections;
    }
}
