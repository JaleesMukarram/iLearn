package com.openlearning.ilearn.quiz.client.repositories;


import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.quiz.admin.modals.Subject;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SubjectRepositoryClient {

    public static SubjectRepositoryClient instance;

    private static final String TAG = "SbjRepoTAG";
    private final FirebaseFirestore db;
    public static final String SUBJECT_COLLECTION = "iLearnSubjects";
    private final List<Subject> subjectList;


    private SubjectRepositoryClient() {

        this.db = FirebaseFirestore.getInstance();
        subjectList = new ArrayList<>();

    }

    public static SubjectRepositoryClient getInstance() {

        if (instance == null) {

            instance = new SubjectRepositoryClient();
        }

        return instance;
    }

    public void getSubjectsFromDatabase(boolean fromServer, FireStoreObjectGetListener listener) {

        if (!fromServer && subjectList != null && subjectList.size() > 0) {

            listener.onSuccess(subjectList);
            Log.d(TAG, "getSubjectsFromDatabase: Old list returned");
            return;
        }

        db.collection(SUBJECT_COLLECTION)
                .whereEqualTo("active", true)
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "getSubjectsFromDatabase: news getting failed: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    subjectList.clear();

                    if (Objects.requireNonNull(task.getResult()).size() > 0) {

                        for (DocumentSnapshot snapshot : task.getResult()) {

                            Subject subject = snapshot.toObject(Subject.class);
                            assert subjectList != null;
                            subjectList.add(subject);

                        }

                        listener.onSuccess(subjectList);
                        Log.d(TAG, "getSubjectsFromDatabase: total subjects: " + subjectList.size());

                    } else {

                        Log.d(TAG, "getSubjectsFromDatabase: No News found");
                        listener.onSuccess(null);
                    }

                });

    }

}
