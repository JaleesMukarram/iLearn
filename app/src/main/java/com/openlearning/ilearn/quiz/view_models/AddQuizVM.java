package com.openlearning.ilearn.quiz.view_models;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.dialogues.LoadingDialogue;
import com.openlearning.ilearn.quiz.modals.Quiz;
import com.openlearning.ilearn.quiz.repositories.QuizRepository;
import com.openlearning.ilearn.quiz.repositories.SubjectRepository;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.utils.CommonUtils;

import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_LARGE;
import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_MEDIUM;
import static com.openlearning.ilearn.utils.CommonUtils.MIN_LENGTH_SMALL;
import static com.openlearning.ilearn.utils.CommonUtils.STRING_EMPTY;
import static com.openlearning.ilearn.utils.CommonUtils.VALIDATION_SUCCESS;


public class AddQuizVM extends ViewModel {

    private static final String TAG = "AddQuizVMTAG";
    private final QuizRepository quizRepository;
    private final SubjectRepository subjectRepository;
    private String subjectID;
    private LoadingDialogue loadingDialogue;

    public AddQuizVM() {

        subjectRepository = SubjectRepository.getInstance();
        quizRepository = QuizRepository.getInstance();
        subjectID = "89fa472b-a0e0-42e4-a8f7-ab02b6097e04";

    }

    public void checkQuiz(Activity activity, String quizName, String quizDescription, String quizTime) {

        String quizNameStatus = validateQuizName(quizName);
        if (!quizNameStatus.equals(VALIDATION_SUCCESS)) {
            CommonUtils.showWarningDialogue(activity, quizNameStatus);
            return;
        }


        String quizDescriptionStatus = validateQuizDescription(quizDescription);
        if (!quizDescriptionStatus.equals(VALIDATION_SUCCESS)) {
            CommonUtils.showWarningDialogue(activity, quizDescriptionStatus);
            return;
        }

        if (quizTime.equals("") || quizTime.length() == 0) {
            CommonUtils.showWarningDialogue(activity, "Quiz time empty");
            return;
        }


        String quizTimeStatus = validateQuizTime(Integer.parseInt(quizTime));
        if (!quizTimeStatus.equals(VALIDATION_SUCCESS)) {
            CommonUtils.showWarningDialogue(activity, quizTimeStatus);
            return;
        }

        Quiz quiz = makeQuiz(quizName, quizDescription, Integer.parseInt(quizTime));
        addQuizToDatabase(activity, quiz);

    }

    private Quiz makeQuiz(String quizName, String quizDescription, int quizTime) {

        return new Quiz(quizName, quizDescription, quizTime, subjectID);
    }

    private void addQuizToDatabase(Activity activity, Quiz quiz) {

        showLoadingDialogue(activity);

        quizRepository.insertNewQuiz(quiz, new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                loadingDialogue.cancel();
                CommonUtils.showSuccessDialogue(activity, "Quiz added successfully");

            }

            @Override
            public void onFailure(Exception ex) {

                loadingDialogue.cancel();
                CommonUtils.showDangerDialogue(activity, ex.getLocalizedMessage());

            }
        });


    }

    private void showLoadingDialogue(Activity activity) {

        loadingDialogue = new LoadingDialogue(activity);
        loadingDialogue.ready("Please wait...", "Please wait while we are adding new quiz");
        loadingDialogue.show();
    }

    public String validateQuizName(String quizName) {

        Log.d(TAG, "Quiz Name validation started");

        if (quizName == null || quizName.equals(STRING_EMPTY)) {

            String status = "Quiz Name Empty";
            Log.d(TAG, status);
            return status;

        } else if (quizName.length() < MIN_LENGTH_SMALL) {

            String status = "Quiz Name less than " + MIN_LENGTH_SMALL + " characters";
            Log.d(TAG, status);
            return status;

        } else if (quizName.length() > MAX_LENGTH_MEDIUM) {

            String status = "Quiz Name greater than " + MAX_LENGTH_MEDIUM + " characters";
            Log.d(TAG, status);
            return status;

        }

        return VALIDATION_SUCCESS;

    }

    public String validateQuizDescription(String quizDescription) {

        Log.d(TAG, "Subject Description validation started");

        if (quizDescription == null || quizDescription.equals(STRING_EMPTY)) {

            String status = "Quiz Description Empty";
            Log.d(TAG, status);
            return status;

        } else if (quizDescription.length() < MIN_LENGTH_SMALL) {

            String status = "Quiz Description less than " + MIN_LENGTH_SMALL + " characters";
            Log.d(TAG, status);
            return status;

        } else if (quizDescription.length() > MAX_LENGTH_LARGE) {

            String status = "Quiz Description greater than " + MAX_LENGTH_LARGE + " characters";
            Log.d(TAG, status);
            return status;

        }

        return VALIDATION_SUCCESS;

    }

    public String validateQuizTime(int quizTime) {

        Log.d(TAG, "Quiz Description validation started");

        if (quizTime <= 0) {

            String status = "Quiz Time cannot be 0";
            Log.d(TAG, status);
            return status;

        } else if (quizTime > 180) {

            String status = "Quiz Time more than 3 hours ";
            Log.d(TAG, status);
            return status;

        }

        return VALIDATION_SUCCESS;

    }

}
