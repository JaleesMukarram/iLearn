package com.openlearning.ilearn.article.view_models;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.article.modals.Article;
import com.openlearning.ilearn.article.repositories.ArticleRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;


public class AllSubjectsArticlesVM extends ViewModel {

    private final ArticleRepository articleRepository;
    private final MutableLiveData<List<Article>> articleList;
    private final MutableLiveData<Boolean> articleEmpty;
    private Subject subject;

    public AllSubjectsArticlesVM() {
        articleRepository = new ArticleRepository();
        articleList = new MutableLiveData<>();
        articleEmpty = new MutableLiveData<>();
    }

    public void getAllArticleForThisSubject(Activity activity) {

        articleRepository.getWritersArticlesFromDatabase(UserRegistration.getInstance().getUserID(), subject.getId(), new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    articleList.setValue((List<Article>) obj);

                } else {

                    articleEmpty.setValue(true);
                }
            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showDangerDialogue(activity, "Failed to load news\n" + ex.getLocalizedMessage());

            }
        });

    }

    public void initIDs(Subject subject) {
        this.subject = subject;
    }

    public MutableLiveData<List<Article>> getArticleList() {
        return articleList;
    }

    public MutableLiveData<Boolean> getArticleEmpty() {
        return articleEmpty;
    }
}
