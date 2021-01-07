package com.openlearning.ilearn.quiz.admin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityAddQuizBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.news.StatusDialogue;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.admin.view_models.AddQuizVM;

public class AddQuiz extends AppCompatActivity implements ActivityHooks {

    private ActivityAddQuizBinding mBinding;
    private AddQuizVM viewModel;
    private Subject quizSubject;

    private Quiz editQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_quiz);

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

        if (quizSubject == null) finish();

        editQuiz = getIntent().getParcelableExtra(Quiz.PARCELABLE_KEY);

    }

    @Override
    public void init() {

        viewModel = ViewModelProviders.of(this).get(AddQuizVM.class);
        viewModel.setQuizSubject(quizSubject);

        if (editQuiz != null) {

            viewModel.setEditQuiz(editQuiz);
            mBinding.setQuiz(editQuiz);
            mBinding.BTNAddQuiz.setText(R.string.edit_quiz);

        }

        mBinding.BTNAddQuiz.setOnClickListener(v -> viewModel.checkQuiz(this,
                mBinding.ETQuizName.getText().toString().trim(),
                mBinding.ETQuizDescription.getText().toString(),
                mBinding.ETQuizDuration.getText().toString().trim(),
                mBinding.SWPractive.isChecked()
        ));

        mBinding.IVThreeDots.setOnClickListener(v -> showStatusDialogue());
    }

    @Override
    public void process() {

    }

    @Override
    public void loaded() {

    }

    private void showStatusDialogue() {

        boolean active = true;

        if (editQuiz != null) {

            active = editQuiz.isActive();

        }

        StatusDialogue dialogue = new StatusDialogue(this);
        dialogue.ready(active, editQuiz != null, new StatusDialogue.StatusListener() {
            @Override
            public void onStatusChanged(boolean active) {

                viewModel.setActive(active);
            }

            @Override
            public void onDelete() {

//                viewModel.deleteEditNews(AddNews.this);

            }
        });

        dialogue.show();
    }
}