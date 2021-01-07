package com.openlearning.ilearn.quiz.client.repositories;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.QuizAttempted;
import com.openlearning.ilearn.quiz.admin.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.admin.modals.QuizSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.openlearning.ilearn.quiz.admin.repositories.SubjectRepository.SUBJECT_COLLECTION;
import static com.openlearning.ilearn.utils.CommonUtils.STRING_EMPTY;

public class QuizRepositoryClient {

    private static QuizRepositoryClient instance;
    public static final String QUIZ_COLLECTION = "iLearnSubjectQuiz";
    public static final String QUIZ_SECTION_COLLECTION = "iLearnSubjectQuizSection";
    public static final String QUIZ_SECTION_QUESTION_COLLECTION = "iLearnSubjectQuizSectionQuestion";

    public static final String QUIZ_ATTEMPT_COLLECTION = "iLearnQuizAttempt";

    private static final String TAG = "QuizRepoTAG";
    private final FirebaseFirestore db;

    private final List<Quiz> quizList;
    private final List<QuizSection> quizSectionList;


    private String lastSubjectID = STRING_EMPTY;

    private QuizRepositoryClient() {

        db = FirebaseFirestore.getInstance();
        quizList = new ArrayList<>();
        quizSectionList = new ArrayList<>();
    }

    public static QuizRepositoryClient getInstance() {

        if (instance == null) {

            instance = new QuizRepositoryClient();
        }

        return instance;
    }

    public void getQuizFromDatabase(boolean fromServer, String subjectID, FireStoreObjectGetListener listener) {

        if (!fromServer && lastSubjectID.equals(subjectID) && quizList.size() > 0) {

            listener.onSuccess(quizList);
            Log.d(TAG, "getQuizFromDatabase: Old list returned");
            return;
        }

        lastSubjectID = subjectID;

        db.collection(SUBJECT_COLLECTION)
                .document(subjectID)
                .collection(QUIZ_COLLECTION)
                .whereEqualTo("active", true)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "getQuizFromDatabase: quiz getting failed: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    Objects.requireNonNull(quizList).clear();

                    if (Objects.requireNonNull(task.getResult()).size() > 0) {

                        for (DocumentSnapshot snapshot : task.getResult()) {

                            Quiz quiz = snapshot.toObject(Quiz.class);
                            assert quiz != null;
                            quizList.add(quiz);

                        }

                        Log.d(TAG, "getQuizFromDatabase: total quiz " + quizList.size());
                        listener.onSuccess(quizList);

                    } else {

                        Log.d(TAG, "getQuizFromDatabase: No Quiz found");
                        listener.onSuccess(null);
                    }

                });

    }

    public void getQuizSectionsFromDatabase(String subjectID, String quizID, FireStoreObjectGetListener listener) {

        db.collection(SUBJECT_COLLECTION)
                .document(subjectID)
                .collection(QUIZ_COLLECTION)
                .document(quizID)
                .collection(QUIZ_SECTION_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "getQuizSectionsFromDatabase: Quiz Sections getting failed");
                        listener.onFailure(task.getException());
                        return;
                    }

                    quizSectionList.clear();

                    if (Objects.requireNonNull(task.getResult()).size() > 0) {

                        for (DocumentSnapshot snapshot : task.getResult()) {

                            QuizSection quizSection = snapshot.toObject(QuizSection.class);
                            assert quizSection != null;
                            quizSectionList.add(quizSection);
                        }

                        Log.d(TAG, "getQuizSectionsFromDatabase: total sections: " + quizSectionList.size());
                        listener.onSuccess(quizSectionList);

                    } else {

                        Log.d(TAG, "getQuizSectionsFromDatabase: No Section found");
                        listener.onSuccess(null);
                    }
                });

    }

    public void getQuizSectionQuestion(Quiz quiz, String sectionID, FireStoreObjectGetListener listener) {

        db.collection(SUBJECT_COLLECTION)
                .document(quiz.getQuizSubjectID())
                .collection(QUIZ_COLLECTION)
                .document(quiz.getQuizID())
                .collection(QUIZ_SECTION_COLLECTION)
                .document(sectionID)
                .collection(QUIZ_SECTION_QUESTION_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "getQuizSectionQuestion: Quiz Questions getting failed");
                        listener.onFailure(task.getException());
                        return;
                    }

                    List<QuizQuestion> quizQuestionList = new ArrayList<>();

                    if (Objects.requireNonNull(task.getResult()).size() > 0) {

                        for (DocumentSnapshot snapshot : task.getResult()) {

                            QuizQuestion quizQuestion = snapshot.toObject(QuizQuestion.class);
                            quizQuestionList.add(quizQuestion);

                        }

                        Log.d(TAG, "getQuizSectionQuestion: total question for section: " + sectionID + " " + quizQuestionList.size());
                        listener.onSuccess(quizQuestionList);

                    } else {

                        Log.d(TAG, "getQuizSectionQuestion: No News found");
                        listener.onSuccess(null);
                    }
                });

    }

    public void insertNewQuizAttempt(QuizAttempted quizAttempted, FirebaseSuccessListener listener) {

        db.collection(QUIZ_ATTEMPT_COLLECTION)
                .document(UUID.randomUUID().toString())
                .set(quizAttempted)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "insertNewQuizAttempt: failed to insert new Quiz Attempt: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(quizAttempted);
                    Log.d(TAG, "insertNewQuizAttempt: Quiz Attempt added successfully");
                });

    }

    public void getAllAttemptsOfThisQuiz(String quizID, FireStoreObjectGetListener listener) {

        db.collection(QUIZ_ATTEMPT_COLLECTION)
                .whereEqualTo("quizID", quizID)
                .orderBy("obtainedMarks", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "getAllAttemptsOfThisQuiz: quiz attempts getting failed: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    List<QuizAttempted> quizAttemptedList = new ArrayList<>();

                    if (Objects.requireNonNull(task.getResult()).size() > 0) {

                        for (DocumentSnapshot snapshot : task.getResult()) {

                            QuizAttempted quizAttempted = snapshot.toObject(QuizAttempted.class);
                            quizAttemptedList.add(quizAttempted);

                        }

                        Log.d(TAG, "getAllAttemptsOfThisQuiz: total quiz attempts " + quizAttemptedList.size());
                        listener.onSuccess(quizAttemptedList);

                    } else {

                        Log.d(TAG, "getAllAttemptsOfThisQuiz: No Quiz Attempt found");
                        listener.onSuccess(null);
                    }

                });

    }

}
