package com.openlearning.ilearn.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityHomeScreenInstructorBinding;
import com.openlearning.ilearn.dialogues.AccountOptionDialogue;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.news.AllNews;
import com.openlearning.ilearn.quiz.activities.AllSubjects;
import com.openlearning.ilearn.utils.CommonUtils;
import com.openlearning.ilearn.view_models.HomeScreenInstructorVM;

public class HomeScreenInstructor extends AppCompatActivity implements ActivityHooks {

    private ActivityHomeScreenInstructorBinding mBinding;
    private HomeScreenInstructorVM viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen_instructor);

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

        viewModel = ViewModelProviders.of(this).get(HomeScreenInstructorVM.class);

        AccountOptionsListener accountOptionsListener = new AccountOptionsListener();
        mBinding.IVAccountTypeIcon.setOnClickListener(accountOptionsListener);
        mBinding.TVUserName.setOnClickListener(accountOptionsListener);
        mBinding.TVUserType.setOnClickListener(accountOptionsListener);

        mBinding.CVManageNews.setOnClickListener(v -> CommonUtils.changeActivity(this, AllNews.class, false));
        mBinding.CVManageSubject.setOnClickListener(v -> CommonUtils.changeActivity(this, AllSubjects.class, false));
    }

    @Override
    public void process() {

        mBinding.setUser(viewModel.getCurrentUser());
    }

    @Override
    public void loaded() {

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