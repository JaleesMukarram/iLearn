package com.openlearning.ilearn.quiz.repositories;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.openlearning.ilearn.quiz.modals.Subject;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SubjectRepository {

    private static final String TAG = "SbjRepoTAG";
    public static SubjectRepository instance;
    private final FirebaseFirestore db;
    public static final String SUBJECT_COLLECTION = "iLearnSubjects";
    private final List<Subject> subjectList;

    private SubjectRepository() {

        this.db = FirebaseFirestore.getInstance();
        subjectList = new ArrayList<>();
    }

    public static SubjectRepository getInstance() {

        if (instance == null) {

            return new SubjectRepository();
        }

        return instance;
    }

    public void insertNewSubject(Subject newSubject, FirebaseSuccessListener listener) {

        db.collection(SUBJECT_COLLECTION)
                .document(newSubject.getId())
                .set(newSubject)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "insertNewSubject: failed to insert new Subject: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(newSubject);
                    Log.d(TAG, "insertNewSubject: Subject added successfully");
                });

    }

    public void getSubjectsFromDatabase(boolean fromServer, FireStoreObjectGetListener listener) {

        if (!fromServer && subjectList != null && subjectList.size() > 0) {

            listener.onSuccess(subjectList);
            Log.d(TAG, "getSubjectsFromDatabase: Old list returned");
            return;
        }

        db.collection(SUBJECT_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "getNewsFromDatabase: news getting failed");
                        listener.onFailure(task.getException());
                        return;
                    }

                    if (Objects.requireNonNull(task.getResult()).size() > 0) {

                        for (DocumentSnapshot snapshot : task.getResult()) {

                            Subject subject = snapshot.toObject(Subject.class);
                            assert subjectList != null;
                            subjectList.add(subject);

                        }

                        listener.onSuccess(subjectList);

                    } else {

                        Log.d(TAG, "getNewsFromDatabase: No News found");
                        listener.onSuccess(null);
                    }

                });

    }


}
