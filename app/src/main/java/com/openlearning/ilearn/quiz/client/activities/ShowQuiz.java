package com.openlearning.ilearn.quiz.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.article.adapters.ArticleAdapter;
import com.openlearning.ilearn.article.modals.Article;
import com.openlearning.ilearn.chat.activities.SendChat;
import com.openlearning.ilearn.chat.queries.Chat;
import com.openlearning.ilearn.databinding.ActivityShowQuizBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.client.adapters.QuizAdapterClient;
import com.openlearning.ilearn.quiz.client.view_models.ShowQuizVM;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class ShowQuiz extends AppCompatActivity implements ActivityHooks {

    private ActivityShowQuizBinding mBinding;
    private ShowQuizVM viewModel;
    private Subject quizSubject;
    private List<Quiz> quizList;

    private ArticleAdapter articleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_show_quiz);

        callHooks();
    }

    @Override
    public void callHooks() {

        handleIntent();
        init();
        process();

    }

    @Override
    public void handleIntent() {

        quizSubject = getIntent().getParcelableExtra(Subject.PARCELABLE_KEY);
        if (quizSubject == null) finish();

    }

    @Override
    public void init() {

        viewModel = ViewModelProviders.of(this).get(ShowQuizVM.class);
        viewModel.initIDs(quizSubject);

        mBinding.SRLQuizRefresh.setOnRefreshListener(() -> {

            viewModel.getQuiz(this, true);
            viewModel.getAllArticleForThisSubject(this);
        });
        mBinding.BTNLiveChat.setOnClickListener(v -> {

            Intent intent = new Intent(this, SendChat.class);
            intent.putExtra(Chat.RECEIVING_USER_ID_KEY, quizSubject.getInstructorID());
            CommonUtils.changeActivity(this, intent, false);

        });

    }

    @Override
    public void process() {

        viewModel.getQuizList().observe(this, quizzes -> {

            quizList = quizzes;
            loaded();
            mBinding.SRLQuizRefresh.setRefreshing(false);
            showQuizRecycler();
        });
        viewModel.getQuizEmpty().observe(this, aBoolean -> {

            if (aBoolean) {
                loaded();
                mBinding.SRLQuizRefresh.setRefreshing(false);
            }
        });

        viewModel.getArticleList().observe(this, articleList -> {

            if (articleAdapter == null) {
                loaded();
                showArticleRecycler(articleList);
            } else {
                articleAdapter.notifyDataSetChanged();
            }


            mBinding.SRLQuizRefresh.setRefreshing(false);
        });


        viewModel.getQuiz(this, false);
        viewModel.getAllArticleForThisSubject(this);


    }

    @Override
    public void loaded() {

        mBinding.SRLQuizRefresh.setVisibility(View.VISIBLE);
        mBinding.PBRLoading.setVisibility(View.GONE);

    }

    private void showQuizRecycler() {

        QuizAdapterClient quizAdapter = new QuizAdapterClient(this, quizSubject, quizList);
        mBinding.RVAllQuizRecycler.setAdapter(quizAdapter);

    }

    private void showArticleRecycler(List<Article> articleList) {

        articleAdapter = new ArticleAdapter(this, quizSubject, articleList);
        mBinding.RVAllArticleRecycler.setAdapter(articleAdapter);

    }

}