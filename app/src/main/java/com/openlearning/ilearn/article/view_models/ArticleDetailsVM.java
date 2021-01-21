package com.openlearning.ilearn.article.view_models;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.article.modals.Article;
import com.openlearning.ilearn.article.repositories.ArticleRepository;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.modals.PostComment;
import com.openlearning.ilearn.modals.PostReact;
import com.openlearning.ilearn.registration.UserRegistration;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.openlearning.ilearn.utils.CommonUtils.addMyReact;
import static com.openlearning.ilearn.utils.CommonUtils.isMyReactDone;
import static com.openlearning.ilearn.utils.CommonUtils.removeMyReact;

public class ArticleDetailsVM extends ViewModel {

    private static final String TAG = "ArticleDetTAG";
    private final ArticleRepository articleRepository;
    private final UserRegistration userRegistration;
    private final MutableLiveData<Boolean> likeStatusChanged;
    private final MutableLiveData<Boolean> commentStatusChanged;
    private Article article;

    public ArticleDetailsVM() {
        articleRepository = new ArticleRepository();
        userRegistration = UserRegistration.getInstance();
        likeStatusChanged = new MutableLiveData<>();
        commentStatusChanged = new MutableLiveData<>();
        likeStatusChanged.setValue(true);
        commentStatusChanged.setValue(true);
    }

    public void initIDs(Article article) {

        this.article = article;

    }

    public void manageLike() {

        Log.d(TAG, "manageLike: ");

        if (likeStatusChanged.getValue() != null && !likeStatusChanged.getValue()) {
            return;
        }

        likeStatusChanged.setValue(false);

        if (isMyReactDone(article.getPostReactList(), userRegistration.getUserID())) {

            removeMyReact(article.getPostReactList(), userRegistration.getUserID());
            Log.d(TAG, "manageLike: my like removed");
        } else {

            addMyReact(article.getPostReactList(), userRegistration.getUserID(), PostReact.REACT_LIKE);
            Log.d(TAG, "manageLike: my like added");
        }

        articleRepository.updateReactListForThisArticle(article.getId(), article.getPostReactList(), new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                likeStatusChanged.setValue(true);
                Log.d(TAG, "onSuccess: ");

            }

            @Override
            public void onFailure(Exception ex) {

                likeStatusChanged.setValue(true);
                Log.d(TAG, "onFailure: ");

            }
        });
    }

    public String getUserID() {

        return userRegistration.getUserID();
    }

    public void manageNewComment(String comment) {

        Log.d(TAG, "manageNewComment: ");

        if (commentStatusChanged.getValue() != null && !commentStatusChanged.getValue()) {
            return;
        }

        commentStatusChanged.setValue(false);


        PostComment postComment = new PostComment(getUserID(), comment);
        articleRepository.addReactCommentForThisArticle(article.getId(), postComment, new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                Log.d(TAG, "onSuccess: comment added");
                article.getPostCommentList().add(postComment);
                commentStatusChanged.setValue(true);

            }

            @Override
            public void onFailure(Exception ex) {

                Log.d(TAG, "onSuccess: comment failed: " + ex);
                commentStatusChanged.setValue(true);

            }
        });

    }

    public void sortAllComments(List<PostComment> postCommentList) {

        Collections.sort(postCommentList, (o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()));
    }


    public MutableLiveData<Boolean> getLikeStatusChanged() {
        return likeStatusChanged;
    }

    public MutableLiveData<Boolean> getCommentStatusChanged() {
        return commentStatusChanged;
    }

    public void getNameOfThisUser(String userID, FirebaseSuccessListener listener) {

        userRegistration.getNameOfThisUser(userID, listener);
    }

    public void deleteThisComment(PostComment postComment) {

        articleRepository.removeReactCommentForThisArticle(article.getId(), postComment, new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                article.getPostCommentList().remove(postComment);
                commentStatusChanged.setValue(true);

            }

            @Override
            public void onFailure(Exception ex) {

            }
        });
    }
}
