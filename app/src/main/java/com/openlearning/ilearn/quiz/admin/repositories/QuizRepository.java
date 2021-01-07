package com.openlearning.ilearn.quiz.admin.repositories;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.admin.modals.QuizSection;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.openlearning.ilearn.quiz.admin.repositories.SubjectRepository.SUBJECT_COLLECTION;

public class QuizRepository {

    public static final String QUIZ_COLLECTION = "iLearnSubjectQuiz";
    public static final String QUIZ_SECTION_COLLECTION = "iLearnSubjectQuizSection";
    public static final String QUIZ_SECTION_QUESTION_COLLECTION = "iLearnSubjectQuizSectionQuestion";

    private static final String TAG = "QuizRepoTAG";
    public static QuizRepository instance;
    private final FirebaseFirestore db;

    private final List<Quiz> quizList;
    private final List<QuizSection> quizSectionList;
    private final List<QuizQuestion> quizQuestionList;

    private QuizRepository() {

        db = FirebaseFirestore.getInstance();
        quizList = new ArrayList<>();
        quizSectionList = new ArrayList<>();
        quizQuestionList = new ArrayList<>();
    }

    public static QuizRepository getInstance() {

        if (instance == null) {

            instance = new QuizRepository();
        }

        return instance;
    }

    public void insertNewQuiz(Quiz newQuiz, FirebaseSuccessListener listener) {

        db.collection(SUBJECT_COLLECTION)
                .document(newQuiz.getQuizSubjectID())
                .collection(QUIZ_COLLECTION)
                .document(newQuiz.getQuizID())
                .set(newQuiz)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "insertNewQuiz: failed to insert new Quiz: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(newQuiz);
                    Log.d(TAG, "insertNewQuiz: Subject added successfully");
                });

    }

    public void updateQuiz(Quiz quiz, FirebaseSuccessListener listener) {

        db.collection(SUBJECT_COLLECTION)
                .document(quiz.getQuizSubjectID())
                .collection(QUIZ_COLLECTION)
                .document(quiz.getQuizID())
                .set(quiz, SetOptions.merge())
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "updateQuiz: failed to update Quiz: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(quiz);
                    Log.d(TAG, "updateQuiz: Quiz updated successfully");
                });

    }


    public void insertNewQuizSection(QuizSection quizSection, Quiz quiz, FirebaseSuccessListener listener) {

        db.collection(SUBJECT_COLLECTION)
                .document(quiz.getQuizSubjectID())
                .collection(QUIZ_COLLECTION)
                .document(quiz.getQuizID())
                .collection(QUIZ_SECTION_COLLECTION)
                .document(quizSection.getId())
                .set(quizSection)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "insertNewQuizSection: failed to insert new Quiz: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(quiz);
                    Log.d(TAG, "insertNewQuizSection: Subject added successfully");
                });

    }

    public void insertNewQuizQuestion(QuizQuestion quizQuestion, Quiz quiz, String quizSectionId, FirebaseSuccessListener listener) {

        db.collection(SUBJECT_COLLECTION)
                .document(quiz.getQuizSubjectID())
                .collection(QUIZ_COLLECTION)
                .document(quiz.getQuizID())
                .collection(QUIZ_SECTION_COLLECTION)
                .document(quizSectionId)
                .collection(QUIZ_SECTION_QUESTION_COLLECTION)
                .document(quizQuestion.getID())
                .set(quizQuestion)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "insertNewQuizQuestion: failed to insert new Question: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(quizQuestion);
                    Log.d(TAG, "insertNewQuizQuestion: Question added successfully");
                });

    }

    public void updateQuizQuestion(QuizQuestion quizQuestion, Quiz quiz, String quizSectionId, FirebaseSuccessListener listener) {

        db.collection(SUBJECT_COLLECTION)
                .document(quiz.getQuizSubjectID())
                .collection(QUIZ_COLLECTION)
                .document(quiz.getQuizID())
                .collection(QUIZ_SECTION_COLLECTION)
                .document(quizSectionId)
                .collection(QUIZ_SECTION_QUESTION_COLLECTION)
                .document(quizQuestion.getID())
                .set(quizQuestion, SetOptions.merge())
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "updateQuizQuestion: failed to edit Question: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(quizQuestion);
                    Log.d(TAG, "insertNewQuizQuestion: Question edited successfully");
                });

    }

    public void getQuizFromDatabase(boolean fromServer, String subjectID, FireStoreObjectGetListener listener) {

        if (!fromServer && quizList != null && quizList.size() > 0) {

            listener.onSuccess(quizList);
            Log.d(TAG, "getQuizFromDatabase: Old list returned");
            return;
        }

        db.collection(SUBJECT_COLLECTION)
                .document(subjectID)
                .collection(QUIZ_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "getQuizFromDatabase: news getting failed");
                        listener.onFailure(task.getException());
                        return;
                    }

                    assert quizList != null;
                    quizList.clear();

                    Log.d(TAG, "getQuizFromDatabase: " + task.getResult().size());

                    if (Objects.requireNonNull(task.getResult()).size() > 0) {

                        for (DocumentSnapshot snapshot : task.getResult()) {

                            Quiz quiz = snapshot.toObject(Quiz.class);
                            assert quiz != null;
                            quizList.add(quiz);

                        }

                        listener.onSuccess(quizList);

                    } else {

                        Log.d(TAG, "getNewsFromDatabase: No News found");
                        listener.onSuccess(null);
                    }

                });

    }

    public void listenQuizSectionsFromDatabase(boolean fromServer, String subjectID, String quizID, FireStoreObjectGetListener listener) {

        if (!fromServer && quizSectionList != null && quizSectionList.size() > 0) {

            listener.onSuccess(quizSectionList);
            Log.d(TAG, "getQuizSectionsFromDatabase: Old list returned");
            return;
        }

        db.collection(SUBJECT_COLLECTION)
                .document(subjectID)
                .collection(QUIZ_COLLECTION)
                .document(quizID)
                .collection(QUIZ_SECTION_COLLECTION)
                .addSnapshotListener((querySnapshot, error) -> {

                    if (error != null) {

                        Log.d(TAG, "getQuizSectionsFromDatabase: Quiz getting failed");
                        listener.onFailure(error);
                        return;
                    }

                    quizSectionList.clear();

                    if (Objects.requireNonNull(querySnapshot).size() > 0) {

                        for (DocumentSnapshot snapshot : querySnapshot) {

                            QuizSection quizSection = snapshot.toObject(QuizSection.class);
                            assert quizSection != null;
                            quizSectionList.add(quizSection);

                        }

                        listener.onSuccess(quizSectionList);

                    } else {

                        Log.d(TAG, "getQuizSectionsFromDatabase: No News found");
                        listener.onSuccess(null);
                    }
                });

    }

    public void listenToQuizSectionQuestion(boolean fromServer, Quiz quiz, String sectionID, FireStoreObjectGetListener listener) {

        if (!fromServer && quizSectionList != null && quizSectionList.size() > 0) {

            listener.onSuccess(quizSectionList);
            Log.d(TAG, "getQuizSectionsFromDatabase: Old list returned");
            return;
        }

        db.collection(SUBJECT_COLLECTION)
                .document(quiz.getQuizSubjectID())
                .collection(QUIZ_COLLECTION)
                .document(quiz.getQuizID())
                .collection(QUIZ_SECTION_COLLECTION)
                .document(sectionID)
                .collection(QUIZ_SECTION_QUESTION_COLLECTION)
                .addSnapshotListener((querySnapshot, error) -> {

                    if (error != null) {

                        Log.d(TAG, "listenToQuizSectionQuestion: Quiz getting failed");
                        listener.onFailure(error);
                        return;
                    }

                    quizQuestionList.clear();

                    if (Objects.requireNonNull(querySnapshot).size() > 0) {

                        for (DocumentSnapshot snapshot : querySnapshot) {

                            QuizQuestion quizQuestion = snapshot.toObject(QuizQuestion.class);
                            assert quizQuestion != null;
                            quizQuestionList.add(quizQuestion);

                        }

                        listener.onSuccess(quizQuestionList);

                    } else {

                        Log.d(TAG, "listenToQuizSectionQuestion: No News found");
                        listener.onSuccess(null);
                    }
                });

    }


}
