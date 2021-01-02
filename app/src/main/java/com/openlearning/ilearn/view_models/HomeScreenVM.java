package com.openlearning.ilearn.view_models;


import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.news.News;
import com.openlearning.ilearn.news.NewsRepository;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.registration.User;

import java.util.List;

public class HomeScreenVM extends ViewModel {

    private final UserRegistration userRegistration;
    private final NewsRepository newsRepository;
    private final MutableLiveData<List<News>> newsList;

    public HomeScreenVM() {

        userRegistration = UserRegistration.getInstance();
        newsRepository = NewsRepository.getInstance();
        newsList = new MutableLiveData<>();

    }

    public User getCurrentUser() {

        return userRegistration.getCurrentUserFromDB();
    }

    public void getNews() {

        newsRepository.getNewsFromDatabase(true, new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    newsList.setValue((List<News>) obj);

                }
            }

            @Override
            public void onFailure(Exception ex) {

            }
        });

    }

    public LiveData<List<News>> getNewsList() {
        return newsList;
    }
}
