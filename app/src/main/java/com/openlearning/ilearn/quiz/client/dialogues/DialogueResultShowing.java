package com.openlearning.ilearn.quiz.client.dialogues;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;


import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewQuizResultShowingBinding;
import com.openlearning.ilearn.databinding.ViewSingleSubjectAnalysisBinding;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.quiz.admin.modals.QuizAttempted;
import com.openlearning.ilearn.quiz.client.layers.QuizLogic;
import com.openlearning.ilearn.quiz.client.layers.SectionManager;
import com.openlearning.ilearn.quiz.client.repositories.QuizRepositoryClient;
import com.openlearning.ilearn.registration.UserRegistration;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class DialogueResultShowing {

    private static final String TAG = "DlgResultTAG";
    private final QuizLogic quizLogic;
    private Dialog mDialog;

    // Subject

    private ViewQuizResultShowingBinding mBinding;


    public DialogueResultShowing(QuizLogic quizLogic) {
        this.quizLogic = quizLogic;
        if (!quizLogic.isPractice()) {
            insertQuizAttempt();
        }
    }

    @SuppressLint("InflateParams")
    public void initAndShow() {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(quizLogic.getHomeScreen()), R.layout.view_quiz_result_showing, null, false);

        initListeners();
        addSections();

        populateTopScores();
        populateBoxScores();
        populateProgressScores();
        populateSubjectDistribution();

        mDialog = new Dialog(quizLogic.getHomeScreen(), android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        mDialog.setContentView(mBinding.getRoot());
        mDialog.show();
    }

    private void initListeners() {

        mBinding.IVBack.setOnClickListener(v -> mDialog.cancel());

        mBinding.BTNViewQuestions.setOnClickListener(v -> mDialog.cancel());
    }

    private void addSections() {

        for (int i = 0; i < quizLogic.getQuiz().getQuizSectionList().size(); i++) {

            DialgueSectionResult result = new DialgueSectionResult(quizLogic.getSectionManager(), i);
            result.initializeViews();
            result.addToThisView(mBinding.LLAppender);

        }
    }

    private void populateTopScores() {

        mBinding.TVObtainedScore.setText(String.valueOf(quizLogic.getObtainedScore()));
        mBinding.TVTotalScore.setText(String.valueOf(quizLogic.getTotalQuizMarks()));

    }

    private void populateBoxScores() {

        mBinding.TVTotalQuestionsShowing.setText(String.valueOf(quizLogic.getTotalNumberOfQuestionsOfThisQuiz()));
        mBinding.TVTotalMarksShowing.setText(String.valueOf(quizLogic.getTotalQuizMarks()));
        mBinding.TVTotalAttemptedShowing.setText(String.valueOf(quizLogic.getTotalAttempted()));

        DecimalFormat precision = new DecimalFormat("0.00");
        mBinding.TVTotalNegativeShowing.setText(precision.format(quizLogic.getNegativeMarks()));


    }

    private void populateProgressScores() {

        int totalQuestions = quizLogic.getTotalNumberOfQuestionsOfThisQuiz();
        int skipped = totalQuestions - quizLogic.getTotalAttempted();

        int correctPercentage = quizLogic.getTotalCorrectPercent();
        int incorrectPercentage = quizLogic.getTotalInCorrectPercent();

        int skippedPercentage = (int) (skipped / (1f * totalQuestions) * 100);

        String cp = correctPercentage + "%";
        String icp = incorrectPercentage + "%";
        String sp = skippedPercentage + "%";

        mBinding.TVCorrectPercent.setText(cp);
        mBinding.TVInCorrectPercent.setText(icp);
        mBinding.TVSkipPercent.setText(sp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            mBinding.PBRCorrect.setProgress(correctPercentage, true);
            mBinding.PBRIncorrect.setProgress(incorrectPercentage, true);
            mBinding.PBRSkipped.setProgress(skippedPercentage, true);


        } else {

            mBinding.PBRCorrect.setProgress(correctPercentage);
            mBinding.PBRIncorrect.setProgress(incorrectPercentage);
            mBinding.PBRSkipped.setProgress(skippedPercentage);

        }


        mBinding.TVAccurateCount.setText(String.valueOf(quizLogic.getAccuracyPercentOfThisQuiz()));
        mBinding.TVCorrectCount.setText(String.valueOf(correctPercentage));
        mBinding.TVInCorrectCount.setText(String.valueOf(incorrectPercentage));
        mBinding.TVAttemptedCount.setText(String.valueOf(100 - skippedPercentage));

    }

    private void populateSubjectDistribution() {

        SectionManager manager = quizLogic.getSectionManager();

        for (int i = 0; i < quizLogic.getQuiz().getQuizSectionList().size(); i++) {


            addToView(mBinding.RLAllAccurate, quizLogic.getQuiz().getQuizSectionList().get(i).getName(), manager.getAccuracyOfThisSection(i));
            addToView(mBinding.RLAllAttempted, quizLogic.getQuiz().getQuizSectionList().get(i).getName(), manager.getTotalAnsweredOfThisSection(i));
            addToView(mBinding.RLAllCorrect, quizLogic.getQuiz().getQuizSectionList().get(i).getName(), manager.getTotalSectionCorrectCount(i));
            addToView(mBinding.RLAllInCorrect, quizLogic.getQuiz().getQuizSectionList().get(i).getName(), manager.getTotalSectionInCorrectCount(i));

        }
    }

    private void addToView(LinearLayout appender, String sectionName, int value) {

        ViewSingleSubjectAnalysisBinding binding = DataBindingUtil.inflate(LayoutInflater.from(appender.getContext()), R.layout.view_single_subject_analysis, appender, false);

        binding.TVSubjectName.setText(sectionName);
        binding.TVSubjectScore.setText(String.valueOf(value));

        appender.addView(binding.getRoot());

    }

    private void insertQuizAttempt() {

        if (quizLogic.isRankUploaded()) {

            Log.d(TAG, "insertQuizAttempt: rank already uploaded");
            getMyRank();
            return;
        }

        QuizAttempted quizAttempted = new QuizAttempted(UserRegistration.getInstance().getUserID(), Objects.requireNonNull(quizLogic.getQuiz().getQuizID()), quizLogic.getObtainedScore());
        QuizRepositoryClient.getInstance().insertNewQuizAttempt(quizAttempted, new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                quizLogic.setRankUploaded(true);
                getMyRank();

            }

            @Override
            public void onFailure(Exception ex) {
            }
        });

    }

    private void getMyRank() {

        QuizRepositoryClient.getInstance().getAllAttemptsOfThisQuiz(quizLogic.getQuiz().getQuizID(), new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    List<QuizAttempted> quizAttemptedList = (List<QuizAttempted>) obj;
                    Log.d(TAG, "onSuccess: " + quizAttemptedList.size());
                    getRankFromList(quizAttemptedList);

                }
            }

            @Override
            public void onFailure(Exception ex) {

            }
        });
    }

    private void getRankFromList(List<QuizAttempted> quizAttemptedList) {

        if (mBinding != null) {

            double myMarks = quizLogic.getObtainedScore();
            for (int i = 0; i < quizAttemptedList.size(); i++) {

                if (quizAttemptedList.get(i).getObtainedMarks() == myMarks) {

                    mBinding.TVMyRank.setText(String.valueOf(i + 1));
                    mBinding.TVTotalRank.setText(String.valueOf(quizAttemptedList.size()));

                    return;
                }
            }
        }
    }
}
