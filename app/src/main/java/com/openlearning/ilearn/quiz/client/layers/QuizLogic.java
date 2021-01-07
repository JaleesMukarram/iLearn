package com.openlearning.ilearn.quiz.client.layers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager2.widget.ViewPager2;

import com.openlearning.ilearn.databinding.ActivityQuizAttemptBinding;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.client.activities.QuizActivity;
import com.openlearning.ilearn.quiz.client.adapter.SectionAdapter;
import com.openlearning.ilearn.quiz.client.dialogues.DialogueQuizStart;
import com.openlearning.ilearn.quiz.client.dialogues.DialogueResultShowing;
import com.openlearning.ilearn.utils.CommonUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class QuizLogic {

    private static final String TAG = "QuizCoreTAG";
    private final Activity homeScreen;
    private final ActivityQuizAttemptBinding mBinding;

    public boolean quizCompleted;
    private Quiz quiz;
    private SectionManager sectionManager;
    private boolean rankUploaded;
    private SectionAdapter sectionAdapter;
    private int totalTimeSeconds;
    private Timer timer;


    public QuizLogic(Activity homeScreen, ActivityQuizAttemptBinding mBinding, Quiz quiz) {
        this.homeScreen = homeScreen;
        this.mBinding = mBinding;
        this.quiz = quiz;
    }

    public void callHooks() {

        DialogueQuizStart start = new DialogueQuizStart(this);
        start.initAndShow();
    }

    public void startQuiz() {

        initViews();
        initListeners();
        process();

    }

    public void initViews() {

        sectionManager = new SectionManager(this);
        sectionManager.initializeSection();

        sectionAdapter = new SectionAdapter(this, sectionManager);
        mBinding.RVQuizNewAllSectionsShowing.setAdapter(sectionAdapter);
    }

    public void initListeners() {

        mBinding.IVOptionsIcon.setOnClickListener(view -> sectionManager.showSectionSieView());
        mBinding.IVBackIcon.setOnClickListener(view -> homeScreen.onBackPressed());

    }

    public void process() {

        mBinding.TVQuizName.setText(quiz.getQuizName());

        if (!quiz.isPractice())
            startTimer(quiz.getQuizDurationMin() * 60);
    }

    private void startTimer(int totalTime) {

        totalTimeSeconds = totalTime;

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                quiz.incrementQuizPassedTimesSeconds();

                homeScreen.runOnUiThread(() -> {

                    String time = getRemainingTime();
                    mBinding.TVQuizTime.setText(time);

//                        if (hasSectionTime) {
//

//
//                        }


                });

            }
        }, 1000, 1000);

    }

    private String getRemainingTime() {

        String time = "HH:MM:SS";
        int diff = totalTimeSeconds - quiz.getPassedTimedSeconds();

        if (diff == 0 && !quiz.isPractice()) {

            timer.purge();
            timer.cancel();
//            onQuizFinish();
            return "Time finished";
        }

        int hours = diff / 3600;
        int minutes = (diff - (3600 * hours)) / 60;
        int seconds = (diff - ((3600 * hours) + (60 * minutes)));

        if (seconds % 5 == 0) {

            saveQuizToItsFile();
        }

        time = time.replace("HH", String.valueOf(hours));
        time = time.replace("MM", String.valueOf(minutes));
        time = time.replace("SS", String.valueOf(seconds));


        return time;

    }

    private void saveQuizToItsFile() {

        try {

            FileOutputStream fos = homeScreen.openFileOutput(String.valueOf(quiz.getQuizID()), Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(quiz);

            Log.d(TAG, "saveQuizToItsFile: Quiz Saved");

        } catch (IOException e) {

            Log.e(TAG, "saveQuizToItsFile: Error: " + e);

        }
    }

    public void checkIfSavedVersionIsAvailable() {

        try {

            FileInputStream fis = homeScreen.openFileInput(String.valueOf(quiz.getQuizID()));
            ObjectInputStream ois = new ObjectInputStream(fis);
            Quiz savedQuiz = (Quiz) ois.readObject();

            if (savedQuiz != null) {

                showQuizResumeDialogue(savedQuiz);

            } else {

                Log.d(TAG, "checkIfSavedVersionIsAvailable: No Quiz Found");
                startQuiz();
            }


        } catch (IOException | ClassNotFoundException e) {
            Log.d(TAG, "checkIfSavedVersionIsAvailable: File error");
            startQuiz();
        }

    }

    private void deleteQuizFromFile() {

        homeScreen.deleteFile(String.valueOf(quiz.getQuizID()));

    }

    private void showQuizResumeDialogue(final Quiz savedQuiz) {

        AlertDialog.Builder alertConf = new AlertDialog.Builder(homeScreen);

        alertConf.setTitle("Confirmation");
        alertConf.setMessage("Your previous unfinished quiz is saved, Do you want to continue that");
        alertConf.setPositiveButton("Resume", (dialogInterface, i) -> {

            quiz = savedQuiz;
            startQuiz();
            Toast.makeText(homeScreen, "Quiz Resumed", Toast.LENGTH_SHORT).show();

        });
        alertConf.setNegativeButton("New Quiz", (dialogInterface, i) -> startQuiz());

        AlertDialog dialog = alertConf.create();
        dialog.show();
    }


    // Getters
    public Quiz getQuiz() {
        return quiz;
    }

    public SectionManager getSectionManager() {
        return sectionManager;
    }

    public Activity getHomeScreen() {
        return homeScreen;
    }

    public ViewPager2 getAllQuestionsPager() {
        return mBinding.VP2llQuestionsShowing;
    }

    public SectionAdapter getSectionAdapter() {
        return sectionAdapter;
    }

    public void confirmThisSectionSubmission(int position) {

        if (quizCompleted) {

            sectionManager.selectThisIndexedSection(position);
            return;
        }

        if (position == sectionManager.getSectionIndex()) {

            return;
        }

        sectionManager.lockCurrentAndMoveTo(position);

//        if (quiz.getQuizSectionList().get(position).getRemainingTime() <= 0) {
//
//            Toast.makeText(homeScreen, "Section Locked", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        DialgueSectionResult dialgueSectionResult = new DialgueSectionResult(sectionManager, sectionManager.getSectionIndex());
//        dialgueSectionResult.initViews();
//        dialgueSectionResult.makeConfirmationOne(position);
//        dialgueSectionResult.showDialogue();

    }

    public boolean isPractice() {
        return quiz.isPractice();
    }

    public void setRankUploaded(boolean rankUploaded) {
        this.rankUploaded = rankUploaded;
    }

    public boolean isRankUploaded() {
        return rankUploaded;
    }

    public void onQuizFinish() {

        quizCompleted = true;
        deleteQuizFromFile();

        if (timer != null) {

            timer.purge();
            timer.cancel();
        }

        mBinding.TVQuizTime.setVisibility(View.GONE);
//
//        sectionPerformance.setVisibility(View.VISIBLE);
//        sectionPerformance.setOnClickListener(v -> {
//
////            DialgueSectionResult result = new DialgueSectionResult(sectionManager, sectionManager.getSectionIndex());
////            result.initViews();
////            result.showDialogue();
//
//        });

        showResult();
    }

    public void showResult() {

        DialogueResultShowing resultShowing = new DialogueResultShowing(this);
        resultShowing.initAndShow();

        sectionManager.showAllResult();

    }

    public int getTotalCorrectPercent() {

        int correct = 0;
        for (int i = 0; i < quiz.getQuizSectionList().size(); i++) {

            correct += sectionManager.getTotalSectionCorrectCount(i);

        }

        Log.d(TAG, "getTotalCorrectPercent: " + (int) (correct / (getTotalAttempted() * 1f) * 100));

        Log.d(TAG, "Correct: " + correct + " Total: " + getTotalAttempted());
        return (int) (correct / (getTotalAttempted() * 1f) * 100);

    }

    public int getTotalInCorrectPercent() {

        int incorrect = 0;
        for (int i = 0; i < quiz.getQuizSectionList().size(); i++) {

            incorrect += sectionManager.getTotalSectionInCorrectCount(i);

        }

        Log.d(TAG, "Incorrect: " + incorrect + " Total: " + getTotalAttempted());
        Log.d(TAG, "getTotalInCorrectPercent: " + (int) (incorrect / (getTotalAttempted() * 1f) * 100));

        return (int) (incorrect / (getTotalAttempted() * 1f) * 100);
    }

    public int getTotalAttempted() {

        int answered = 0;
        for (int i = 0; i < quiz.getQuizSectionList().size(); i++) {

            answered += sectionManager.getTotalAnsweredOfThisSection(i);

        }

        return answered;
    }

    public int getTotalNumberOfQuestionsOfThisQuiz() {

        return quiz.getTotalQuestions();
    }

    public double getTotalQuizMarks() {

        return quiz.getTotalMarks();
    }

    public double getNegativeMarks() {

        double negative = 0;
        for (int i = 0; i < quiz.getQuizSectionList().size(); i++) {

            negative += sectionManager.getNegativeMarksForThisSection(i);

        }

        return negative;

    }

    public float getObtainedScore() {

        float score = 0;
        for (int i = 0; i < quiz.getQuizSectionList().size(); i++) {

            score += sectionManager.getObtainedMarksForThisSection(i);

        }

        Log.d(TAG, "getObtainedScore: " + score);

        return score;
    }

    public int getAccuracyPercentOfThisQuiz() {

        int accuracy = 0;

        for (int i = 0; i < quiz.getQuizSectionList().size(); i++) {

            accuracy += sectionManager.getAccuracyOfThisSection(i);

        }

        return accuracy / quiz.getQuizSectionList().size();
    }


}
