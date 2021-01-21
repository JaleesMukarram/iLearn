package com.openlearning.ilearn.news;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.modals.PostReact;
import com.openlearning.ilearn.registration.UserRegistration;

import static com.openlearning.ilearn.utils.CommonUtils.addMyReact;
import static com.openlearning.ilearn.utils.CommonUtils.isMyReactDone;
import static com.openlearning.ilearn.utils.CommonUtils.removeMyReact;

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

    void initIDs(News news) {
        this.news = news;
    }

    public void manageLike() {

        Log.d(TAG, "manageLike: ");

        if (likeStatusChanged.getValue() != null && !likeStatusChanged.getValue()) {
            return;
        }

        likeStatusChanged.setValue(false);

        if (isMyReactDone(news.getPostReactList(), userRegistration.getUserID())) {

            removeMyReact(news.getPostReactList(), userRegistration.getUserID());
            Log.d(TAG, "manageLike: my like removed");
        } else {

            addMyReact(news.getPostReactList(), userRegistration.getUserID(), PostReact.REACT_LIKE);
            Log.d(TAG, "manageLike: my like added");
        }

        newsRepository.updateReactListForThisNews(news.getId(), news.getPostReactList(), new FirebaseSuccessListener() {
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

    public MutableLiveData<Boolean> getLikeStatusChanged() {
        return likeStatusChanged;
    }

}
