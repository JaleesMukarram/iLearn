package com.openlearning.ilearn.quiz.client.view_models;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.client.repositories.QuizRepositoryClient;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class ShowQuizVM extends ViewModel {

    private final QuizRepositoryClient quizRepositoryClient;
    private final MutableLiveData<List<Quiz>> quizList;
    private final MutableLiveData<Boolean> quizEmpty;
    private Subject subject;

    public ShowQuizVM() {

        quizRepositoryClient = QuizRepositoryClient.getInstance();
        quizList = new MutableLiveData<>();
        quizEmpty = new MutableLiveData<>();
    }

    public void getQuiz(Activity activity, boolean fromServer) {

        quizRepositoryClient.getQuizFromDatabase(fromServer, subject.getId(), new FireStoreObjectGetListener() {
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

                CommonUtils.showWarningDialogue(activity, "There was error while fetching quiz\n" + ex.getLocalizedMessage());

            }
        });


    }

    public void initIDs(Subject subject) {
        this.subject = subject;

    }

    public MutableLiveData<Boolean> getQuizEmpty() {
        return quizEmpty;
    }

    public MutableLiveData<List<Quiz>> getQuizList() {
        return quizList;
    }
}
