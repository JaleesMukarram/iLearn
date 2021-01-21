package com.openlearning.ilearn.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
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
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.client.activities.ShowSubjects;
import com.openlearning.ilearn.quiz.client.adapters.SubjectAdapterClient;
import com.openlearning.ilearn.quiz.client.view_models.ShowSubjectsVM;
import com.openlearning.ilearn.utils.CommonUtils;
import com.openlearning.ilearn.view_models.HomeScreenVM;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity implements ActivityHooks {

    private static final String TAG = "HomeScreenTAG";
    private ActivityHomeScreenBinding mBinding;
    private HomeScreenVM viewModel;

    private List<News> newsList;
    private List<Subject> subjectList;

    private NewsAdapter newsAdapter;

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

        mBinding.TVShowAllSubjects.setOnClickListener(v -> CommonUtils.changeActivity(this, ShowSubjects.class, false));

    }

    @Override
    public void process() {

        mBinding.setUser(viewModel.getCurrentUser());

        viewModel.getNewsList().observe(this, news -> {

            newsList = news;
            showNewsRecycler();
        });
        viewModel.getNews(this);

        viewModel.getSubjectsList().observe(this, subjects -> {

            subjectList = subjects.size() > 3 ? subjects.subList(0, 3) : subjects;
            showSubjectRecycler();

        });

        viewModel.getSubjects(this, false);

    }

    @Override
    public void loaded() {

    }

    private void showNewsRecycler() {

        if (newsAdapter == null) {
            newsAdapter = new NewsAdapter(this, newsList);
            mBinding.RVAllNewsRecycler.setAdapter(newsAdapter);
        } else {
            newsAdapter.notifyDataSetChanged();
        }

    }

    private void showAccountDialogue() {

        AccountOptionDialogue dialogue = new AccountOptionDialogue(this, viewModel.getCurrentUser());
        dialogue.ready();
        dialogue.show();

    }

    private void showSubjectRecycler() {

        SubjectAdapterClient subjectAdapter = new SubjectAdapterClient(this, subjectList, SubjectAdapterClient.FOR_STUDENT);
        mBinding.RVAllSubjectsRecycler.setAdapter(subjectAdapter);

    }

    private class AccountOptionsListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            showAccountDialogue();
        }
    }

}