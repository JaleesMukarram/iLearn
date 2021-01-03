package com.openlearning.ilearn.quiz.admin.view_models;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.repositories.QuizRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class AllQuizVM extends ViewModel {

    private final QuizRepository quizRepository;
    private final MutableLiveData<List<Quiz>> quizList;
    private final MutableLiveData<Boolean> quizEmpty;
    private String subjectID;

    public AllQuizVM() {

        quizRepository = QuizRepository.getInstance();
        quizList = new MutableLiveData<>();
        quizEmpty = new MutableLiveData<>();
    }

    public void getQuizOfSubject(Activity activity, boolean fromServer) {

        quizRepository.getQuizFromDatabase(fromServer, subjectID, new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    quizList.setValue((List<Quiz>) obj);
                } else {

                    quizEmpty.setValue(true);
                }
            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showDangerDialogue(activity, "Failed to load quizzes\n" + ex.getLocalizedMessage());

            }
        });

    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public MutableLiveData<List<Quiz>> getQuizList() {
        return quizList;
    }

    public MutableLiveData<Boolean> getQuizEmpty() {
        return quizEmpty;
    }
}
