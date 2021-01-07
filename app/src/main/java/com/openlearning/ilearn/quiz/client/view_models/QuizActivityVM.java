package com.openlearning.ilearn.quiz.client.view_models;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.client.repositories.QuizMakingRepository;
import com.openlearning.ilearn.quiz.client.repositories.QuizRepositoryClient;


public class QuizActivityVM extends ViewModel {

    public static final String TAG = "QuizAttemptVMTAG";
    public final QuizRepositoryClient quizRepositoryClient;

    private final MutableLiveData<Quiz> completeQuiz;
    private final MutableLiveData<String> quizError;


    public QuizActivityVM() {

        quizRepositoryClient = QuizRepositoryClient.getInstance();
        completeQuiz = new MutableLiveData<>();
        quizError = new MutableLiveData<>();

    }

    public void makeQuizFromDatabase(Quiz quiz) {

        Log.d(TAG, "makeQuizFromDatabase: starting call to make quiz");

        QuizMakingRepository quizMakingRepository = new QuizMakingRepository(quiz, new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                completeQuiz.setValue((Quiz) obj);

                Log.d(TAG, "onSuccess: " + completeQuiz);
            }

            @Override
            public void onFailure(Exception ex) {

                Log.d(TAG, "onFailure: " + ex.getLocalizedMessage());
                quizError.setValue(ex.getLocalizedMessage());
            }
        });

        quizMakingRepository.makeQuiz();
    }


    public MutableLiveData<Quiz> getCompleteQuiz() {
        return completeQuiz;
    }

    public MutableLiveData<String> getQuizError() {
        return quizError;
    }


}
