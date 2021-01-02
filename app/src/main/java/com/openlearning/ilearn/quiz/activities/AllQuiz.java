package com.openlearning.ilearn.quiz.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityAllQuizBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.quiz.adapters.QuizAdapter;
import com.openlearning.ilearn.quiz.adapters.SubjectAdapter;
import com.openlearning.ilearn.quiz.modals.Quiz;
import com.openlearning.ilearn.quiz.modals.Subject;
import com.openlearning.ilearn.quiz.view_models.AllQuizVM;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class AllQuiz extends AppCompatActivity implements ActivityHooks {

    private ActivityAllQuizBinding mBinding;
    private AllQuizVM viewModel;
    private Subject subject;
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

        subject = getIntent().getParcelableExtra(Subject.PARCELABLE_KEY);

        if (subject == null) {

            finish();
        }

    }

    @Override
    public void init() {

        quizList = new ArrayList<>();

        viewModel = ViewModelProviders.of(this).get(AllQuizVM.class);

        mBinding.BTNAddQuiz.setOnClickListener(v -> CommonUtils.changeActivity(this, AddQuiz.class, true));

    }

    @Override
    public void process() {

        viewModel.setSubjectID(subject.getId());

        viewModel.getQuizList().observe(this, quizzes -> {


            quizList = quizzes;
            showNewsRecycler();
        });

        viewModel.getQuizOfSubject();

    }

    @Override
    public void loaded() {

    }

    private void showNewsRecycler() {

        QuizAdapter quizAdapter = new QuizAdapter(this, quizList);
        mBinding.RVAllQuizRecycler.setAdapter(quizAdapter);

    }
}