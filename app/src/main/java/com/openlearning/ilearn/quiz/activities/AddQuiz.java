package com.openlearning.ilearn.quiz.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityAddQuizBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.quiz.view_models.AddQuizVM;

public class AddQuiz extends AppCompatActivity implements ActivityHooks {

    private ActivityAddQuizBinding mBinding;
    private AddQuizVM viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_quiz);

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

        viewModel = ViewModelProviders.of(this).get(AddQuizVM.class);

        mBinding.BTNAddQuiz.setOnClickListener(v -> viewModel.checkQuiz(this,
                mBinding.ETQuizName.getText().toString().trim(),
                mBinding.ETQuizDescription.getText().toString(),
                mBinding.ETQuizDuration.getText().toString().trim()

        ));

    }

    @Override
    public void process() {

    }

    @Override
    public void loaded() {

    }
}