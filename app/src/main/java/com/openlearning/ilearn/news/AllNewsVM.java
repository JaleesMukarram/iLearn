package com.openlearning.ilearn.news;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class AllNewsVM extends ViewModel {

    private final NewsRepository newsRepository;
    private final UserRegistration userRegistration;
    private final MutableLiveData<List<News>> newsList;
    private final MutableLiveData<Boolean> newsEmpty;

    public AllNewsVM() {

        newsRepository = NewsRepository.getInstance();
        userRegistration = UserRegistration.getInstance();
        newsList = new MutableLiveData<>();
        newsEmpty = new MutableLiveData<>();
    }

    public void getNews(Activity activity, boolean fromServer) {

        newsRepository.getNewsFromDatabase(fromServer, userRegistration.getUserID(), new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    newsList.setValue((List<News>) obj);
                } else {

                    newsEmpty.setValue(true);
                }
            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showDangerDialogue(activity, "Failed to load news\n" + ex.getLocalizedMessage());

            }
        });

    }

    public MutableLiveData<List<News>> getNewsList() {
        return newsList;
    }

    public MutableLiveData<Boolean> getNewsEmpty() {
        return newsEmpty;
    }
}
