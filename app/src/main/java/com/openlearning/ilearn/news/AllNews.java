package com.openlearning.ilearn.news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityAllNewsBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class AllNews extends AppCompatActivity implements ActivityHooks {

    private static final String TAG = "AllNewsTAG";
    private ActivityAllNewsBinding mBinding;
    private AllNewsVM viewModel;
    private List<News> newsList;

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

        newsList = new ArrayList<>();
        viewModel = ViewModelProviders.of(this).get(AllNewsVM.class);
        mBinding.SRLNewsRefresh.setOnRefreshListener(() -> viewModel.getNews(this, true));

    }

    @Override
    public void process() {

        viewModel.getNewsList().observe(this, news -> {

            Log.d(TAG, "process: observed");
            newsList = news;
            loaded();
            showNewsRecycler();
            mBinding.SRLNewsRefresh.setRefreshing(false);

        });
        viewModel.getNewsEmpty().observe(this, aBoolean -> {

            if (aBoolean) {
                loaded();
                CommonUtils.showWarningDialogue(this, "No News Found. Your added News will be displayed here");
            }

        });
        viewModel.getNews(this, false);
        mBinding.BTNAddNews.setOnClickListener(v -> CommonUtils.changeActivity(this, AddNews.class, false));

    }

    @Override
    public void loaded() {

        mBinding.PBRLoading.setVisibility(View.GONE);
        mBinding.SRLNewsRefresh.setVisibility(View.VISIBLE);

    }

    private void showNewsRecycler() {

        NewsAdapterAdmin newsAdapterAdmin = new NewsAdapterAdmin(this, newsList);
        mBinding.RVAllNewsRecycler.setAdapter(newsAdapterAdmin);

    }

}