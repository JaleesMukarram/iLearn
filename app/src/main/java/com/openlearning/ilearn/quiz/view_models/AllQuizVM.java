package com.openlearning.ilearn.quiz.view_models;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.quiz.modals.Quiz;
import com.openlearning.ilearn.quiz.repositories.QuizRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;

import java.util.List;

public class AllQuizVM extends ViewModel {

    private final QuizRepository quizRepository;
    private final MutableLiveData<List<Quiz>> quizList;
    private String subjectID;

    public AllQuizVM() {

        quizRepository = QuizRepository.getInstance();
        quizList = new MutableLiveData<>();
    }

    public void getQuizOfSubject() {

        quizRepository.getQuizFromDatabase(false, subjectID, new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    quizList.setValue((List<Quiz>) obj);
                }
            }

            @Override
            public void onFailure(Exception ex) {

            }
        });

    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public MutableLiveData<List<Quiz>> getQuizList() {
        return quizList;
    }



}
