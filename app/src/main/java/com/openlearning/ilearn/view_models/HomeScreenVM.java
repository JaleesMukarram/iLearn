package com.openlearning.ilearn.view_models;


import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.news.News;
import com.openlearning.ilearn.news.NewsRepository;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.admin.repositories.SubjectRepository;
import com.openlearning.ilearn.quiz.client.repositories.SubjectRepositoryClient;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.registration.User;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class HomeScreenVM extends ViewModel {

    private final UserRegistration userRegistration;
    private final NewsRepository newsRepository;
    private final MutableLiveData<List<News>> newsList;

    private final MutableLiveData<List<Subject>> subjectsList;
    private final MutableLiveData<Boolean> subjectsEmpty;
    private final SubjectRepositoryClient subjectRepositoryClient;

    public HomeScreenVM() {

        userRegistration = UserRegistration.getInstance();
        newsRepository = NewsRepository.getInstance();
        subjectRepositoryClient = SubjectRepositoryClient.getInstance();

        newsList = new MutableLiveData<>();

        subjectsList = new MutableLiveData<>();
        subjectsEmpty = new MutableLiveData<>();

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

    public void getSubjects(Activity activity, boolean fromServer) {

        subjectRepositoryClient.getSubjectsFromDatabase(fromServer, new FireStoreObjectGetListener() {

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    subjectsList.setValue((List<Subject>) obj);
                } else {

                    subjectsEmpty.setValue(true);
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

    public MutableLiveData<List<Subject>> getSubjectsList() {
        return subjectsList;
    }

    public MutableLiveData<Boolean> getSubjectsEmpty() {
        return subjectsEmpty;
    }
}
