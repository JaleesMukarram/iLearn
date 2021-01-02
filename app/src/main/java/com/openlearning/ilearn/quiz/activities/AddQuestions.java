package com.openlearning.ilearn.quiz.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityAddQuestionsBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.quiz.adapters.QuizAdapter;
import com.openlearning.ilearn.quiz.adapters.QuizQuestionAdapter;
import com.openlearning.ilearn.quiz.dialogues.AddQuestionDialogue;
import com.openlearning.ilearn.quiz.modals.Quiz;
import com.openlearning.ilearn.quiz.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.modals.QuizSection;
import com.openlearning.ilearn.quiz.view_models.AddQuestionsVM;

import java.util.ArrayList;
import java.util.List;

public class AddQuestions extends AppCompatActivity implements ActivityHooks {

    private QuizSection quizSection;
    private Quiz quiz;
    private ActivityAddQuestionsBinding mBinding;
    private AddQuestionsVM viewModel;
    private List<QuizQuestion> quizQuestionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_questions);

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

        Intent intent = getIntent();
        quiz = intent.getParcelableExtra(Quiz.PARCELABLE_KEY);
        quizSection = intent.getParcelableExtra(QuizSection.PARCELABLE_KEY);

        if (quiz == null || quizSection == null) finish();

    }

    @Override
    public void init() {

        quizQuestionList = new ArrayList<>();

        viewModel = ViewModelProviders.of(this).get(AddQuestionsVM.class);
        viewModel.initIDs(quiz, quizSection);

        mBinding.BTNAddQuestion.setOnClickListener(v -> showQuestionDialogue());
    }

    @Override
    public void process() {

        viewModel.getQuestionsList().observe(this, quizQuestions -> {

            quizQuestionList = quizQuestions;
            showQuestionsRecycler();


        });
        viewModel.getAllSectionQuestions(this);

    }

    @Override
    public void loaded() {

    }

    private void showQuestionDialogue() {

        AddQuestionDialogue dialogue = new AddQuestionDialogue(this, quiz);
        dialogue.ready(question -> viewModel.addQuizQuestion(this, question));

        dialogue.show();

    }

    private void showQuestionsRecycler() {

        QuizQuestionAdapter quizAdapter = new QuizQuestionAdapter(this, quiz, quizSection, quizQuestionList);
        mBinding.RVAllQuizQuestionsRecycler.setAdapter(quizAdapter);

    }


}