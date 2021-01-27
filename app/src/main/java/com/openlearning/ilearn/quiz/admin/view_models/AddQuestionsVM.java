package com.openlearning.ilearn.quiz.admin.view_models;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.admin.modals.QuizSection;
import com.openlearning.ilearn.quiz.admin.repositories.QuizRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class AddQuestionsVM extends ViewModel {

    private static final String TAG = "AddQuestionsVMTAG";
    private final QuizRepository quizRepository;
    private Quiz quiz;
    private QuizSection quizSection;
    private final MutableLiveData<List<QuizQuestion>> questionsList;
    private final MutableLiveData<Boolean> questionsEmpty;

    public AddQuestionsVM() {

        quizRepository = QuizRepository.getInstance();
        questionsList = new MutableLiveData<>();
        questionsEmpty = new MutableLiveData<>();
    }

    public void getAllSectionQuestions(Activity activity, boolean fromServer) {

        quizRepository.listenToQuizSectionQuestion(fromServer, quiz, quizSection.getId(), new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    questionsList.setValue((List<QuizQuestion>) obj);

                }else{

                    questionsEmpty.setValue(true);
                }

            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showWarningDialogue(activity, ex.getLocalizedMessage());

            }
        });
    }

    public void addQuizQuestion(Activity activity, QuizQuestion question) {

        quizRepository.insertNewQuizQuestion(question, quiz, quizSection.getId(), new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                CommonUtils.showSuccessDialogue(activity, "Question added successfully");
            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showDangerDialogue(activity, ex.getLocalizedMessage());
            }
        });
    }

    public void deleteQuizQuestion(Activity activity, String quizQuestionID) {

        quizRepository.deleteQuizQuestion(quizQuestionID, quiz, quizSection.getId(), new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                CommonUtils.showSuccessDialogue(activity, "Question Deleted Successfully");
            }

            @Override
            public void onFailure(Exception ex) {

            }
        });
    }

    public void updateQuizQuestion(Activity activity, QuizQuestion question) {

        quizRepository.updateQuizQuestion(question, quiz, quizSection.getId(), new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                CommonUtils.showSuccessDialogue(activity, "Question edited successfully");
            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showDangerDialogue(activity, ex.getLocalizedMessage());
            }
        });
    }


    public void initIDs(Quiz quiz, QuizSection quizSection) {
        this.quiz = quiz;
        this.quizSection = quizSection;

        Log.d(TAG, "initIDs: quiz: " + quiz);
        Log.d(TAG, "initIDs: quizSection: " + quizSection);
    }

    public MutableLiveData<List<QuizQuestion>> getQuestionsList() {
        return questionsList;
    }

    public MutableLiveData<Boolean> getQuestionsEmpty() {
        return questionsEmpty;
    }
}
