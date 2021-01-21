package com.openlearning.ilearn.quiz.admin.view_models;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.article.modals.Article;
import com.openlearning.ilearn.article.repositories.ArticleRepository;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.admin.repositories.QuizRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class AllQuizVM extends ViewModel {

    private final QuizRepository quizRepository;
    private final ArticleRepository articleRepository;
    private final MutableLiveData<List<Quiz>> quizList;
    private final MutableLiveData<Boolean> quizEmpty;
    private final MutableLiveData<List<Article>> articleList;
    private final MutableLiveData<Boolean> articleEmpty;
    private Subject subject;

    public AllQuizVM() {

        quizRepository = QuizRepository.getInstance();
        articleRepository = new ArticleRepository();
        quizList = new MutableLiveData<>();
        quizEmpty = new MutableLiveData<>();

        articleList = new MutableLiveData<>();
        articleEmpty = new MutableLiveData<>();
    }

    public void getQuizOfSubject(Activity activity, boolean fromServer) {

        quizRepository.getQuizFromDatabase(fromServer, subject.getId(), new FireStoreObjectGetListener() {
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

                CommonUtils.showDangerDialogue(activity, "Failed to load quizzes\n" + ex.getLocalizedMessage());

            }
        });

    }

    public void getAllArticleForThisSubject(Activity activity) {

        articleRepository.getSubjectArticlesFromDatabase( subject.getId(), new FireStoreObjectGetListener() {
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

    public MutableLiveData<List<Quiz>> getQuizList() {
        return quizList;
    }

    public MutableLiveData<Boolean> getQuizEmpty() {
        return quizEmpty;
    }
}
