package com.openlearning.ilearn.quiz.view_models;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.quiz.modals.Quiz;
import com.openlearning.ilearn.quiz.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.modals.QuizSection;
import com.openlearning.ilearn.quiz.repositories.QuizRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class AddQuestionsVM extends ViewModel {

    private final QuizRepository quizRepository;
    private Quiz quiz;
    private QuizSection quizSection;
    private final MutableLiveData<List<QuizQuestion>> questionsList;

    public AddQuestionsVM() {

        quizRepository = QuizRepository.getInstance();
        questionsList = new MutableLiveData<>();
    }

    public void getAllSectionQuestions(Activity activity) {

        quizRepository.getQuizSectionQuestionFromDatabase(true, quiz, quizSection.getID(), new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                questionsList.setValue((List<QuizQuestion>) obj);

            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showWarningDialogue(activity, ex.getLocalizedMessage());

            }
        });
    }

    public void addQuizQuestion(Activity activity, QuizQuestion question) {

        quizRepository.insertNewQuizQuestion(question, quiz, quizSection.getID(), new FirebaseSuccessListener() {
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

    public void initIDs(Quiz quiz, QuizSection quizSection) {
        this.quiz = quiz;
        this.quizSection = quizSection;
    }

    public MutableLiveData<List<QuizQuestion>> getQuestionsList() {
        return questionsList;
    }

}
