package com.openlearning.ilearn.article.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.article.adapters.ArticleAdapter;
import com.openlearning.ilearn.article.modals.Article;
import com.openlearning.ilearn.article.view_models.AllSubjectsArticlesVM;
import com.openlearning.ilearn.databinding.ActivityAllSubejctsArticlesBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class AllSubjectsArticles extends AppCompatActivity implements ActivityHooks {

    private ActivityAllSubejctsArticlesBinding mBinding;
    private AllSubjectsArticlesVM viewModel;
    private Subject subject;
    private ArticleAdapter subjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_all_subejcts_articles);

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

        subject = getIntent().getParcelableExtra(Subject.PARCELABLE_KEY);

        if (subject == null) finish();

    }

    @Override
    public void init() {

        viewModel = ViewModelProviders.of(this).get(AllSubjectsArticlesVM.class);
        viewModel.initIDs(subject);

        mBinding.BTNAddArticle.setOnClickListener(v -> {

            Intent intent = new Intent(this, AddSubjectArticle.class);
            intent.putExtra(Subject.PARCELABLE_KEY, subject);
            CommonUtils.changeActivity(this, intent, false);

        });

        mBinding.SRLArticleRefresh.setOnRefreshListener(() -> viewModel.getAllArticleForThisSubject(this));

    }

    @Override
    public void process() {

        viewModel.getArticleList().observe(this, articles -> {

            if (subjectAdapter == null) {
                showArticleRecycler(articles);

            } else {

                subjectAdapter.notifyDataSetChanged();
            }

            loaded();
            mBinding.SRLArticleRefresh.setRefreshing(false);

        });
        viewModel.getArticleEmpty().observe(this, empty -> {

            if (empty) {

                loaded();
                mBinding.SRLArticleRefresh.setRefreshing(false);
                mBinding.RVAllArticleRecycler.setAdapter(null);
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getAllArticleForThisSubject(this);
    }

    @Override
    public void loaded() {

        mBinding.PBRLoading.setVisibility(View.GONE);
        mBinding.SRLArticleRefresh.setVisibility(View.VISIBLE);


    }

    private void showArticleRecycler(List<Article> articleList) {

        subjectAdapter = new ArticleAdapter(this, subject, articleList);
        mBinding.RVAllArticleRecycler.setAdapter(subjectAdapter);

    }
}