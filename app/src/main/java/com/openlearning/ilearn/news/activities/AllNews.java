package com.openlearning.ilearn.news.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityAllNewsBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.news.modals.News;
import com.openlearning.ilearn.news.adapters.NewsAdapterAdmin;
import com.openlearning.ilearn.news.view_model.AllNewsVM;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class AllNews extends AppCompatActivity implements ActivityHooks {

    private static final String TAG = "AllNewsTAG";
    private ActivityAllNewsBinding mBinding;
    private AllNewsVM viewModel;
    private NewsAdapterAdmin newsAdapterAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_all_news);

        callHooks();
    }

    @Override
    public void callHooks() {

        init();
        process();

    }

    @Override
    public void handleIntent() {

    }

    @Override
    public void init() {

        viewModel = ViewModelProviders.of(this).get(AllNewsVM.class);
        mBinding.SRLNewsRefresh.setOnRefreshListener(() -> viewModel.getNews(this));
        mBinding.BTNAddNews.setOnClickListener(v -> CommonUtils.changeActivity(this, AddNews.class, false));

    }

    @Override
    public void process() {

        viewModel.getNewsList().observe(this, news -> {

            if (newsAdapterAdmin == null) {

                showNewsRecycler(news);
            } else {

                newsAdapterAdmin.notifyDataSetChanged();
            }

            loaded();
            mBinding.SRLNewsRefresh.setRefreshing(false);

        });
        viewModel.getNewsEmpty().observe(this, empty -> {

            if (empty) {

                loaded();
                CommonUtils.showWarningDialogue(this, "No News Found. Your added News will be displayed here");
                mBinding.SRLNewsRefresh.setRefreshing(false);
                mBinding.RVAllNewsRecycler.setAdapter(null);

            }
        });

    }

    @Override
    public void loaded() {

        mBinding.PBRLoading.setVisibility(View.GONE);
        mBinding.SRLNewsRefresh.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getNews(this);
    }

    private void showNewsRecycler(List<News> newsList) {

        newsAdapterAdmin = new NewsAdapterAdmin(this, newsList);
        mBinding.RVAllNewsRecycler.setAdapter(newsAdapterAdmin);

    }
}