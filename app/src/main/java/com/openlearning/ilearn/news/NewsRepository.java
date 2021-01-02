package com.openlearning.ilearn.news;

import android.net.Uri;
import android.util.Log;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.utils.StorageUploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.openlearning.ilearn.utils.CommonUtils.APP_STORAGE;

public class NewsRepository {

    public static final String TAG = "NewsRepoTAG";

    public static final String NEWS_COLLECTION = "iLearnNews";
    public static final String STORAGE_DIRECTORY = APP_STORAGE + "iLearnNews";
    public static NewsRepository instance;

    private final FirebaseFirestore db;
    private final List<News> newsList;

    private NewsRepository() {

        Log.d(TAG, "NewsRepository: instance created");
        db = FirebaseFirestore.getInstance();
        newsList = new ArrayList<>();
    }

    public static NewsRepository getInstance() {

        if (instance == null) {

            instance = new NewsRepository();
        }

        return instance;
    }

    public void insertNewsIntoDatabase(News newNews, FirebaseSuccessListener listener) {

        Log.d(TAG, "insertNewsIntoDatabase: inserting " + newNews);
        db.collection(NEWS_COLLECTION)
                .document(newNews.getId())
                .set(newNews)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "insertNewsIntoDatabase: failed to insert new News: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(newNews);
                    Log.d(TAG, "insertNewsIntoDatabase: News added successfully");
                });

    }

    public void getNewsFromDatabase(boolean fromServer, FireStoreObjectGetListener listener) {

        Log.d(TAG, "getNewsFromDatabase: getting all news from server ");

        if (!fromServer && newsList.size() > 0) {

            listener.onSuccess(newsList);
            Log.d(TAG, "getNewsFromDatabase: Old list returned");
            return;
        }

        db.collection(NEWS_COLLECTION)
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "getNewsFromDatabase: news getting failed");
                        listener.onFailure(task.getException());
                        return;
                    }

                    newsList.clear();

                    if (Objects.requireNonNull(task.getResult()).size() > 0) {

                        for (DocumentSnapshot snapshot : task.getResult()) {
                            News news = snapshot.toObject(News.class);
                            newsList.add(news);
                        }

                        Log.d(TAG, "getNewsFromDatabase: total news found: " + newsList.size());
                        listener.onSuccess(newsList);
                    } else {

                        Log.d(TAG, "getNewsFromDatabase: No News found");
                        listener.onSuccess(null);
                    }
                });

    }

    public void getNewsFromDatabase(boolean fromServer, String instructorID, FireStoreObjectGetListener listener) {

        Log.d(TAG, "getNewsFromDatabase: instructorID: " + instructorID);

        if (!fromServer && newsList.size() > 0) {

            listener.onSuccess(newsList);
            Log.d(TAG, "getNewsFromDatabase: Old list returned");
            return;
        }

        db.collection(NEWS_COLLECTION)
                .whereEqualTo("instructorID", instructorID)
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "getNewsFromDatabase: news getting failed");
                        listener.onFailure(task.getException());
                        return;
                    }

                    newsList.clear();

                    if (Objects.requireNonNull(task.getResult()).size() > 0) {

                        for (DocumentSnapshot snapshot : task.getResult()) {
                            News news = snapshot.toObject(News.class);
                            newsList.add(news);
                        }

                        Log.d(TAG, "getNewsFromDatabase: total news found: " + newsList.size());
                        listener.onSuccess(newsList);
                    } else {

                        Log.d(TAG, "getNewsFromDatabase: No News found");
                        listener.onSuccess(null);
                    }
                });

    }

    public void updateThisNewsIntoDatabase(News newNews, FirebaseSuccessListener listener) {

        db.collection(NEWS_COLLECTION)
                .document(newNews.getId())
                .set(newNews, SetOptions.merge())
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "updateThisNewsIntoDatabase: failed to update News: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(newNews);
                    Log.d(TAG, "updateThisNewsIntoDatabase: News edited successfully");
                });

    }

    public void addNewsImageToDatabase(File file, FirebaseSuccessListener listener) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(STORAGE_DIRECTORY);
        StorageUploadTask storageUploadTask = new StorageUploadTask(storageReference, UUID.randomUUID().toString() + ".jpg", Uri.fromFile(file));
        storageUploadTask.setListener(listener);

    }

    public void deleteNewsWithID(String id, FirebaseSuccessListener listener) {

        Log.d(TAG, "deleteNewsWithID: deleting id: " + id);

        db.collection(NEWS_COLLECTION)
                .document(id)
                .delete()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        listener.onFailure(task.getException());
                        Log.d(TAG, "deleteNewsWithID: error while deleting: " + task.getException());
                        return;
                    }

                    Log.d(TAG, "deleteNewsWithID: deleted");
                    listener.onSuccess(true);

                });
    }
}
