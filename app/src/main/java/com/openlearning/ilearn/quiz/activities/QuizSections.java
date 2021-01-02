package com.openlearning.ilearn.quiz.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityQuizSectionsBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.quiz.adapters.QuizSectionAdapter;
import com.openlearning.ilearn.quiz.dialogues.SingleEditDialogue;
import com.openlearning.ilearn.quiz.modals.Quiz;
import com.openlearning.ilearn.quiz.modals.QuizSection;
import com.openlearning.ilearn.quiz.view_models.QuizSectionsVM;

import java.util.ArrayList;
import java.util.List;

public class QuizSections extends AppCompatActivity implements ActivityHooks {

    private Quiz quiz;
    private ActivityQuizSectionsBinding mBinding;
    private QuizSectionsVM viewModel;
    private List<QuizSection> quizSectionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_sections);

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

        if (quiz == null) {

            finish();
        }

    }

    @Override
    public void init() {

        quizSectionList = new ArrayList<>();
        viewModel = ViewModelProviders.of(this).get(QuizSectionsVM.class);

        mBinding.BTNAddSection.setOnClickListener(v -> showSingleFieldDialogue());

    }

    @Override
    public void process() {

        viewModel.initIDs(quiz);
        viewModel.getQuizSections().observe(this, quizSections -> {

            quizSectionList = quizSections;
            showQuizRecycler();
        });
        viewModel.getAllSections(this);

    }

    @Override
    public void loaded() {

    }

    private void showSingleFieldDialogue() {

        SingleEditDialogue dialogue = new SingleEditDialogue(this);
        dialogue.ready("Please enter section name", editField -> viewModel.addNewsQuizSection(this, editField));
        dialogue.show();


    }

    private void showQuizRecycler() {

        QuizSectionAdapter adapter = new QuizSectionAdapter(this, quiz, quizSectionList);
        mBinding.RVAllQuizSectionsRecycler.setAdapter(adapter);

    }
}