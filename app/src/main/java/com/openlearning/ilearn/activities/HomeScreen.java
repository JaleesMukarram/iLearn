package com.openlearning.ilearn.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityHomeScreenBinding;
import com.openlearning.ilearn.dialogues.AccountOptionDialogue;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.news.News;
import com.openlearning.ilearn.news.NewsAdapter;
import com.openlearning.ilearn.view_models.HomeScreenVM;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity implements ActivityHooks {

    private static final String TAG = "HomeScreenTAG";
    private ActivityHomeScreenBinding mBinding;
    private HomeScreenVM viewModel;
    private NewsAdapter newsAdapter;

    private List<News> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);
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

        viewModel = ViewModelProviders.of(this).get(HomeScreenVM.class);

        AccountOptionsListener accountOptionsListener = new AccountOptionsListener();
        mBinding.IVAccountTypeIcon.setOnClickListener(accountOptionsListener);
        mBinding.TVUserName.setOnClickListener(accountOptionsListener);
        mBinding.TVUserType.setOnClickListener(accountOptionsListener);

    }

    @Override
    public void process() {

        mBinding.setUser(viewModel.getCurrentUser());
        viewModel.getNewsList().observe(this, news -> {

            Log.d(TAG, "init: ");
            newsList = news;
            showNewsRecycler();
        });
        viewModel.getNews();

//        mBinding.IVAccountTypeIcon.setOnClickListener(v -> CommonUtils.changeActivity(this, AllSubjects.class, false));

    }

    @Override
    public void loaded() {

    }

    private void showNewsRecycler() {

        newsAdapter = new NewsAdapter(this, newsList);
        mBinding.RVAllNewsRecycler.setAdapter(newsAdapter);

    }

    private void showAccountDialogue() {

        AccountOptionDialogue dialogue = new AccountOptionDialogue(this, viewModel.getCurrentUser());
        dialogue.ready();
        dialogue.show();

    }

    private class AccountOptionsListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            showAccountDialogue();
        }
    }

}