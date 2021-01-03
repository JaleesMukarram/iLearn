package com.openlearning.ilearn.quiz.admin.view_models;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.QuizSection;
import com.openlearning.ilearn.quiz.admin.repositories.QuizRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class AllQuizSectionsVM extends ViewModel {

    private final QuizRepository quizRepository;
    private final MutableLiveData<List<QuizSection>> quizSections;
    private final MutableLiveData<Boolean> quizSectionsEmpty;
    private Quiz quiz;

    public AllQuizSectionsVM() {

        quizRepository = QuizRepository.getInstance();
        quizSections = new MutableLiveData<>();
        quizSectionsEmpty = new MutableLiveData<>();

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

        quizRepository.listenQuizSectionsFromDatabase(true, quiz.getQuizSubjectID(), quiz.getQuizID(), new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    quizSections.setValue((List<QuizSection>) obj);

                } else {

                    quizSectionsEmpty.setValue(true);
                }

            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showWarningDialogue(activity, ex.getLocalizedMessage());

            }
        });
    }

    public void deleteQuizSection(String quizSectionID) {


    }

    public MutableLiveData<List<QuizSection>> getQuizSections() {
        return quizSections;
    }

    public MutableLiveData<Boolean> getQuizSectionsEmpty() {
        return quizSectionsEmpty;
    }


}
