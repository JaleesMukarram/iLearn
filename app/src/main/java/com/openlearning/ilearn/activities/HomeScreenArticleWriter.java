package com.openlearning.ilearn.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.chat.activities.AllChats;
import com.openlearning.ilearn.databinding.ActivityHomeScreenArticleWriterBinding;
import com.openlearning.ilearn.dialogues.AccountOptionDialogue;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.news.modals.News;
import com.openlearning.ilearn.news.adapters.NewsAdapter;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.client.adapters.SubjectAdapterClient;
import com.openlearning.ilearn.utils.CommonUtils;
import com.openlearning.ilearn.view_models.HomeScreenArticleWriterVM;

import java.util.List;

public class HomeScreenArticleWriter extends AppCompatActivity implements ActivityHooks {

    public static final String TAG = "HSArticleTAG";
    private ActivityHomeScreenArticleWriterBinding mBinding;

    private HomeScreenArticleWriterVM viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen_article_writer);
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


        viewModel = ViewModelProviders.of(this).get(HomeScreenArticleWriterVM.class);

        AccountOptionsListener accountOptionsListener = new AccountOptionsListener();
        mBinding.IVAccountTypeIcon.setOnClickListener(accountOptionsListener);
        mBinding.TVUserName.setOnClickListener(accountOptionsListener);
        mBinding.TVUserType.setOnClickListener(accountOptionsListener);

        mBinding.IVConversation.setOnClickListener(v -> CommonUtils.changeActivity(this, AllChats.class, false));


    }

    @Override
    public void process() {

        mBinding.setUser(viewModel.getCurrentUser());

        viewModel.getNewsList().observe(this, this::showNewsRecycler);

        viewModel.getNews();
        viewModel.getSubjectsList().observe(this, this::showSubjectRecycler);

        viewModel.getSubjects(false);

    }

    @Override
    public void loaded() {

    }

    private void showNewsRecycler(List<News> newsList) {

        NewsAdapter newsAdapter = new NewsAdapter(this, newsList);
        mBinding.RVAllNewsRecycler.setAdapter(newsAdapter);

    }

    private void showAccountDialogue() {

        AccountOptionDialogue dialogue = new AccountOptionDialogue(this, viewModel.getCurrentUser());
        dialogue.ready();
        dialogue.show();

    }

    private void showSubjectRecycler(List<Subject> subjectList) {

        SubjectAdapterClient subjectAdapter = new SubjectAdapterClient(this, subjectList, SubjectAdapterClient.FOR_ARTICLE_WRITER);
        mBinding.RVAllSubjectsRecycler.setAdapter(subjectAdapter);

    }

    private class AccountOptionsListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            showAccountDialogue();
        }
    }

}