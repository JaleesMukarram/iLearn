package com.openlearning.ilearn.article.repositories;


import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.openlearning.ilearn.article.modals.Article;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.modals.PostComment;
import com.openlearning.ilearn.modals.PostReact;
import com.openlearning.ilearn.modals.StorageImage;
import com.openlearning.ilearn.news.News;
import com.openlearning.ilearn.utils.StorageUploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.openlearning.ilearn.utils.CommonUtils.APP_STORAGE;

public class ArticleRepository {

    private static final String TAG = "ArticleRepoTAG";
    public static final String ARTICLE_COLLECTION = "iLearnArticle";
    public static final String STORAGE_DIRECTORY = APP_STORAGE + "iLearnAericles";

    private final FirebaseFirestore db;
    private final List<Article> articlesList;

    public ArticleRepository() {

        db = FirebaseFirestore.getInstance();
        articlesList = new ArrayList<>();
    }


    public void insertArticleIntoDatabase(Article newArticle, FirebaseSuccessListener listener) {

        Log.d(TAG, "insertArticleIntoDatabase: inserting " + newArticle);
        db.collection(ARTICLE_COLLECTION)
                .document(newArticle.getId())
                .set(newArticle)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "insertArticleIntoDatabase: failed to insert new News: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(newArticle);
                    Log.d(TAG, "insertArticleIntoDatabase: News added successfully");
                });

    }

    public void updateThisArticleIntoDatabase(Article article, FirebaseSuccessListener listener) {

        Log.d(TAG, "updateThisArticleIntoDatabase: updating " + article);
        db.collection(ARTICLE_COLLECTION)
                .document(article.getId())
                .set(article, SetOptions.merge())
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "updateThisArticleIntoDatabase: failed to insert new Article: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(article);
                    Log.d(TAG, "updateThisArticleIntoDatabase: Article added successfully");
                });

    }


    public void addArticleImageToDatabase(File file, FirebaseSuccessListener listener) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(STORAGE_DIRECTORY);
        StorageUploadTask storageUploadTask = new StorageUploadTask(storageReference, UUID.randomUUID().toString() + ".jpg", Uri.fromFile(file), StorageImage.class);
        storageUploadTask.setListener(listener);

    }

    public void getWritersArticlesFromDatabase(String articleWriterID, String subjectID, FireStoreObjectGetListener listener) {

        Log.d(TAG, "getWritersArticlesFromDatabase: articleWriterID: " + articleWriterID);

        db.collection(ARTICLE_COLLECTION)
                .whereEqualTo("articleWriterID", articleWriterID)
                .whereEqualTo("subjectID", subjectID)
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "getWritersArticlesFromDatabase: article getting failed: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    articlesList.clear();

                    if (Objects.requireNonNull(task.getResult()).size() > 0) {

                        for (DocumentSnapshot snapshot : task.getResult()) {
                            Article article = snapshot.toObject(Article.class);
                            articlesList.add(article);
                        }

                        listener.onSuccess(articlesList);

                    } else {

                        Log.d(TAG, "getWritersArticlesFromDatabase: No Article for this subject found");
                        listener.onSuccess(null);
                    }
                });

    }

    public void getSubjectArticlesFromDatabase(String subjectID, FireStoreObjectGetListener listener) {

        Log.d(TAG, "getWritersArticlesFromDatabase: subjectID: " + subjectID);

        db.collection(ARTICLE_COLLECTION)
                .whereEqualTo("subjectID", subjectID)
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "getWritersArticlesFromDatabase: article getting failed: " + task.getException());
                        listener.onFailure(task.getException());
                        return;
                    }

                    articlesList.clear();

                    if (Objects.requireNonNull(task.getResult()).size() > 0) {

                        for (DocumentSnapshot snapshot : task.getResult()) {
                            Article article = snapshot.toObject(Article.class);
                            articlesList.add(article);
                        }

                        listener.onSuccess(articlesList);

                    } else {

                        Log.d(TAG, "getWritersArticlesFromDatabase: No Article for this subject found");
                        listener.onSuccess(null);
                    }
                });

    }

    public void getActiveSubjectArticlesFromDatabase(String subjectID, FireStoreObjectGetListener listener) {

        Log.d(TAG, "getActiveSubjectArticlesFromDatabase: subjectID: " + subjectID);

        db.collection(ARTICLE_COLLECTION)
                .whereEqualTo("subjectID", subjectID)
                .whereEqualTo("active", true)
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, error) -> {

                    if (error != null) {

                        Log.d(TAG, "getActiveSubjectArticlesFromDatabase: article getting failed: " + error);
                        listener.onFailure(error);
                        return;
                    }


                    articlesList.clear();

                    if (Objects.requireNonNull(querySnapshot).size() > 0) {

                        for (DocumentSnapshot snapshot : querySnapshot) {
                            Article article = snapshot.toObject(Article.class);
                            articlesList.add(article);
                        }

                        listener.onSuccess(articlesList);

                    } else {

                        Log.d(TAG, "getActiveSubjectArticlesFromDatabase: No Article for this subject found");
                        listener.onSuccess(null);
                    }

                });

    }

    public void deleteArticleWithID(String id, FirebaseSuccessListener listener) {

        Log.d(TAG, "deleteArticleWithID: deleting id: " + id);

        db.collection(ARTICLE_COLLECTION)
                .document(id)
                .delete()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        listener.onFailure(task.getException());
                        Log.d(TAG, "deleteArticleWithID: error while deleting: " + task.getException());
                        return;
                    }

                    Log.d(TAG, "deleteArticleWithID: deleted");
                    listener.onSuccess(true);

                });
    }

    public void updateReactListForThisArticle(String articleID, List<PostReact> postReactList, FirebaseSuccessListener listener) {

        Map<String, List<PostReact>> reactMap = new HashMap<>();
        reactMap.put("postReactList", postReactList);

        db.collection(ARTICLE_COLLECTION)
                .document(articleID)
                .set(reactMap, SetOptions.merge())
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        listener.onFailure(task.getException());
                        Log.d(TAG, "updateReactListForThisArticle: failed to updated like: " + task.getException());
                        return;
                    }
                    listener.onSuccess(null);
                });

    }

    public void addReactCommentForThisArticle(String articleID, PostComment postComment, FirebaseSuccessListener listener) {


        db.collection(ARTICLE_COLLECTION)
                .document(articleID)
                .update("postCommentList", FieldValue.arrayUnion(postComment))
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        listener.onFailure(task.getException());
                        Log.d(TAG, "updateReactListForThisArticle: failed to updated like: " + task.getException());
                        return;
                    }

                    listener.onSuccess(null);
                });

    }

    public void removeReactCommentForThisArticle(String articleID, PostComment postComment, FirebaseSuccessListener listener) {

        db.collection(ARTICLE_COLLECTION)
                .document(articleID)
                .update("postCommentList", FieldValue.arrayRemove(postComment))
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        listener.onFailure(task.getException());
                        Log.d(TAG, "updateReactListForThisArticle: failed to updated like: " + task.getException());
                        return;
                    }

                    listener.onSuccess(null);
                });

    }


}
