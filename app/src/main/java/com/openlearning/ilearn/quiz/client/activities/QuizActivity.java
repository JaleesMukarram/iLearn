package com.openlearning.ilearn.quiz.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityQuizAttemptBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.interfaces.ConfirmationListener;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.QuizAttempted;
import com.openlearning.ilearn.quiz.client.layers.QuizLogic;
import com.openlearning.ilearn.quiz.client.view_models.QuizActivityVM;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class QuizActivity extends AppCompatActivity implements ActivityHooks {

    private ActivityQuizAttemptBinding mBinding;
    private QuizActivityVM viewModel;
    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_attempt);

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

        quiz = getIntent().getParcelableExtra(Quiz.PARCELABLE_KEY);

        if (quiz == null) finish();
    }

    @Override
    public void init() {

        viewModel = ViewModelProviders.of(this).get(QuizActivityVM.class);

    }

    @Override
    public void process() {

        viewModel.makeQuizFromDatabase(quiz);

        viewModel.getCompleteQuiz().observe(this, quiz -> {

            QuizLogic logicLayer = new QuizLogic(this, mBinding, quiz);
            logicLayer.callHooks();

        });

        viewModel.getQuizError().observe(this, error -> CommonUtils.showDangerDialogue(this, error));
    }

    @Override
    public void loaded() {

    }

    @Override
    public void onBackPressed() {

        CommonUtils.showConfirmationDialogue(this, "Exit Quiz", "Are you sure to exit", "Exit", new ConfirmationListener() {
            @Override
            public void onPositive() {

                QuizActivity.super.onBackPressed();

            }

            @Override
            public void onNegative() {


            }
        });
    }


}