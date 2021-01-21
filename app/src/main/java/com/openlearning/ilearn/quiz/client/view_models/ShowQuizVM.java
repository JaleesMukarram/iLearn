package com.openlearning.ilearn.quiz.client.view_models;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.openlearning.ilearn.article.modals.Article;
import com.openlearning.ilearn.article.repositories.ArticleRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.client.repositories.QuizRepositoryClient;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class ShowQuizVM extends ViewModel {

    private final QuizRepositoryClient quizRepositoryClient;
    private final ArticleRepository articleRepository;
    private final MutableLiveData<List<Quiz>> quizList;
    private final MutableLiveData<Boolean> quizEmpty;
    private final MutableLiveData<List<Article>> articleList;

    private Subject subject;

    public ShowQuizVM() {

        quizRepositoryClient = QuizRepositoryClient.getInstance();
        articleRepository = new ArticleRepository();
        quizList = new MutableLiveData<>();
        quizEmpty = new MutableLiveData<>();
        articleList = new MutableLiveData<>();

    }

    public void getQuiz(Activity activity, boolean fromServer) {

        quizRepositoryClient.getQuizFromDatabase(fromServer, subject.getId(), new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    quizList.setValue((List<Quiz>) obj);
                } else {

                    quizEmpty.setValue(true);
                }
            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showWarningDialogue(activity, "There was error while fetching quiz\n" + ex.getLocalizedMessage());

            }
        });


    }

    public void initIDs(Subject subject) {
        this.subject = subject;

    }

    public void getAllArticleForThisSubject(Activity activity) {

        articleRepository.getActiveSubjectArticlesFromDatabase(subject.getId(), new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    articleList.setValue((List<Article>) obj);

                }
            }

            @Override
            public void onFailure(Exception ex) {

                CommonUtils.showDangerDialogue(activity, "Failed to load news\n" + ex.getLocalizedMessage());

            }
        });

    }

    public MutableLiveData<Boolean> getQuizEmpty() {
        return quizEmpty;
    }

    public MutableLiveData<List<Quiz>> getQuizList() {
        return quizList;
    }

    public MutableLiveData<List<Article>> getArticleList() {
        return articleList;
    }

}
