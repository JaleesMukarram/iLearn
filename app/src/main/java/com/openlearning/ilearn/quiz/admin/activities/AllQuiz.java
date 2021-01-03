package com.openlearning.ilearn.quiz.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityAllQuizBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.quiz.admin.adapters.QuizAdapter;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.admin.view_models.AllQuizVM;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class AllQuiz extends AppCompatActivity implements ActivityHooks {

    private static final String TAG = "AllQuizTAG";
    private ActivityAllQuizBinding mBinding;
    public AllQuizVM viewModel;
    private Subject quizSubject;
    private List<Quiz> quizList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_all_quiz);

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

        if (quizSubject == null) {

            finish();
        }

    }

    @Override
    public void init() {

        quizList = new ArrayList<>();

        viewModel = ViewModelProviders.of(this).get(AllQuizVM.class);

        mBinding.BTNAddQuiz.setOnClickListener(v -> {

            Intent intent = new Intent(this, AddQuiz.class);
            intent.putExtra(Subject.PARCELABLE_KEY, quizSubject);
            CommonUtils.changeActivity(this, intent, false);
        });

        mBinding.SRLQuizRefresh.setOnRefreshListener(() -> viewModel.getQuizOfSubject(this, true));

    }

    @Override
    public void process() {

        viewModel.setSubjectID(quizSubject.getId());

        viewModel.getQuizList().observe(this, quizzes -> {

            Log.d(TAG, "process: observed");
            quizList = quizzes;
            loaded();
            showQuizRecycler();
            mBinding.SRLQuizRefresh.setRefreshing(false);

        });
        viewModel.getQuizEmpty().observe(this, aBoolean -> {

            if (aBoolean) {
                loaded();
                CommonUtils.showWarningDialogue(this, "No Quiz Found. Your added Quiz will be displayed here");
                mBinding.SRLQuizRefresh.setRefreshing(false);
            }
        });

        viewModel.getQuizOfSubject(this, false);

    }

    @Override
    public void loaded() {

        mBinding.PBRLoading.setVisibility(View.GONE);
        mBinding.SRLQuizRefresh.setVisibility(View.VISIBLE);
    }

    private void showQuizRecycler() {

        QuizAdapter quizAdapter = new QuizAdapter(this, quizSubject, quizList);
        mBinding.RVAllQuizRecycler.setAdapter(quizAdapter);

    }
}