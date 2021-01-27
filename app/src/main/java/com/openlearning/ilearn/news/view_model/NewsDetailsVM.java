package com.openlearning.ilearn.news.view_model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.modals.PostReact;
import com.openlearning.ilearn.news.modals.News;
import com.openlearning.ilearn.news.repositories.NewsRepository;
import com.openlearning.ilearn.registration.UserRegistration;

import static com.openlearning.ilearn.utils.CommonUtils.getMyReact;
import static com.openlearning.ilearn.utils.CommonUtils.isMyReactDone;

public class NewsDetailsVM extends ViewModel {

    private static final String TAG = "NewsDetailsVMTAG";
    private final NewsRepository newsRepository;
    private final UserRegistration userRegistration;
    private final MutableLiveData<Boolean> likeStatusChanged;
    private News news;

    public NewsDetailsVM() {

        newsRepository = NewsRepository.getInstance();
        userRegistration = UserRegistration.getInstance();
        likeStatusChanged = new MutableLiveData<>();
        likeStatusChanged.setValue(true);
    }

    public void initIDs(News news) {
        this.news = news;
    }

    public void manageLike() {

        Log.d(TAG, "manageLike: ");

        if (likeStatusChanged.getValue() != null && !likeStatusChanged.getValue()) {
            return;
        }

        likeStatusChanged.setValue(false);

        PostReact myReact = getMyReact(news.getPostReactList(), userRegistration.getUserID());

        if (myReact != null) {

            Log.d(TAG, "manageLike: my like removed");
            newsRepository.removePostReactForThisNews(news.getId(), myReact, new FirebaseSuccessListener() {
                @Override
                public void onSuccess(Object obj) {

                    news.getPostReactList().remove(myReact);
                    likeStatusChanged.setValue(true);
                }

                @Override
                public void onFailure(Exception ex) {

                    likeStatusChanged.setValue(true);

                }
            });

        } else {

            Log.d(TAG, "manageLike: my like added");
            PostReact myNewReact = new PostReact(userRegistration.getUserID(), PostReact.REACT_LIKE);
            newsRepository.addPostReactForThisNews(news.getId(), myNewReact, new FirebaseSuccessListener() {
                @Override
                public void onSuccess(Object obj) {

                    news.getPostReactList().add(myNewReact);
                    likeStatusChanged.setValue(true);

                }

                @Override
                public void onFailure(Exception ex) {

                    likeStatusChanged.setValue(true);
                }
            });
        }


    }

    public String getUserID() {

        return userRegistration.getUserID();
    }

    public MutableLiveData<Boolean> getLikeStatusChanged() {
        return likeStatusChanged;
    }

}
