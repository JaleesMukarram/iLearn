package com.openlearning.ilearn.quiz.client.repositories;

import android.util.Log;

import androidx.annotation.Nullable;

import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.admin.modals.QuizSection;
import com.openlearning.ilearn.quiz.client.interfaces.IQuizGetting;

import java.util.ArrayList;
import java.util.List;

public class QuizMakingRepository implements IQuizGetting {

    public static final String TAG = "QuizMakingRepoTAG";
    private final Quiz quiz;
    private final QuizRepositoryClient quizRepositoryClient;
    private final FirebaseSuccessListener listener;
    private List<QuizSection> quizSectionList;
    private List<QuizSectionQuestionsTask> allSectionTasks;

    public QuizMakingRepository(Quiz quiz, FirebaseSuccessListener listener) {
        Log.d(TAG, "QuizMakingRepository: object created for quiz: " + quiz.getQuizName());

        this.quiz = quiz;
        this.listener = listener;
        quizRepositoryClient = QuizRepositoryClient.getInstance();
    }

    @Override
    public void makeQuiz() {

        getQuizSections();
    }

    @Override
    public void getQuizSections() {

        Log.d(TAG, "getQuizSections: started getting sections");

        quizRepositoryClient.getQuizSectionsFromDatabase(quiz.getQuizSubjectID(), quiz.getQuizID(), new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    Log.d(TAG, "getQuizSections: success for getting quiz sections");
                    List<QuizSection> list = (List<QuizSection>) obj;
                    quizSectionList = new ArrayList<>(list);
                    getQuizSectionQuestions();


                } else {

                    Log.d(TAG, "onSuccess: No Sections available for this quiz");
                    onNoSectionFound();

                }
            }

            @Override
            public void onFailure(Exception ex) {

                Log.e(TAG, "Failed to load quiz sections: " + ex.getLocalizedMessage());
                onCompleteFailure(ex);

            }
        });

    }

    @Override
    public void onNoSectionFound() {

        onCompleteFailure(new Exception("This quiz has no section"));

    }

    @Override
    public void getQuizSectionQuestions() {

        Log.d(TAG, "getQuizSectionQuestions: getting started");

        allSectionTasks = new ArrayList<>();
        for (QuizSection quizSection : quizSectionList) {

            QuizSectionQuestionsTask quizSectionQuestionsTask = new QuizSectionQuestionsTask(quizSection);
            quizSectionQuestionsTask.getQuestionForThisSection();
            allSectionTasks.add(quizSectionQuestionsTask);
        }
    }

    @Override
    public void onSomeSectionLoaded(String sectionID) {

        boolean someLeft = false;

        for (QuizSectionQuestionsTask task : allSectionTasks) {

            if (!task.questionsLoaded) {

                someLeft = true;
                break;
            }
        }

        if (!someLeft) {
            onCompleteSuccess();
        }
    }

    @Override
    public void onCompleteSuccess() {


        Log.d(TAG, "onCompleteSuccess: everything loaded");
        quiz.setQuizSectionList(quizSectionList);

        Log.d(TAG, "onCompleteSuccess: quiz: " + quiz.toString());
        listener.onSuccess(quiz);

    }

    @Override
    public void onCompleteFailure(Exception ex) {

        Log.d(TAG, "onCompleteFailure: failure occurred" + ex.getLocalizedMessage());
        listener.onFailure(ex);

    }

    private class QuizSectionQuestionsTask {

        private final QuizSection quizSection;
        private boolean questionsLoaded;

        public QuizSectionQuestionsTask(QuizSection quizSection) {
            this.quizSection = quizSection;
            Log.d(TAG, "QuizSectionTask: created task for section: " + quizSection.getName());
        }

        public void getQuestionForThisSection() {

            quizRepositoryClient.getQuizSectionQuestion(quiz, quizSection.getId(), new FireStoreObjectGetListener() {
                @Override
                @SuppressWarnings("unchecked")
                public void onSuccess(@Nullable Object obj) {

                    Log.d(TAG, "getQuestionForThisSection: success for section: " + quizSection.getName());

                    List<QuizQuestion> quizQuestionList = (List<QuizQuestion>) obj;
                    quizSection.setQuestionList(quizQuestionList);

                    questionsLoaded = true;
                    onSomeSectionLoaded(quizSection.getId());
                }

                @Override
                public void onFailure(Exception ex) {

                    onCompleteFailure(new Exception("Failed to get Question for Section: " + quizSection.getName()));

                }
            });
        }
    }
}
