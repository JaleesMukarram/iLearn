package com.openlearning.ilearn.quiz.admin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityAddQuestionsBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.quiz.admin.adapters.QuizQuestionAdapter;
import com.openlearning.ilearn.quiz.admin.dialogues.AddQuestionDialogue;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.admin.modals.QuizSection;
import com.openlearning.ilearn.quiz.admin.view_models.AddQuestionsVM;

import java.util.List;

public class AddQuestions extends AppCompatActivity implements ActivityHooks {

    private static final String TAG = "AddQuestionTAG";
    private Quiz quiz;
    private QuizSection quizSection;
    private ActivityAddQuestionsBinding mBinding;
    private AddQuestionsVM viewModel;
    private QuizQuestionAdapter quizQuestionAdapter;


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

        viewModel = ViewModelProviders.of(this).get(AddQuestionsVM.class);
        viewModel.initIDs(quiz, quizSection);


        mBinding.BTNAddQuestion.setOnClickListener(v -> showQuestionDialogue(null));
    }

    @Override
    public void process() {

        viewModel.getQuestionsList().observe(this, quizQuestions -> {

            if (quizQuestionAdapter == null) {

                showQuestionsRecycler(quizQuestions);

            } else {

                quizQuestionAdapter.notifyDataSetChanged();
            }
        });

        viewModel.getQuestionsEmpty().observe(this, empty -> {

            if (empty) {

                Toast.makeText(this, "No Question Added", Toast.LENGTH_SHORT).show();
                mBinding.RVAllQuizQuestionsRecycler.setAdapter(null);
            }
        });
        viewModel.getAllSectionQuestions(this, true);

    }

    @Override
    public void loaded() {

    }

    private void showQuestionDialogue(QuizQuestion editQuestion) {

        AddQuestionDialogue dialogue = new AddQuestionDialogue(this);
        dialogue.ready((obj, edit) -> {

            QuizQuestion quizQuestion = (QuizQuestion) obj;

            if (edit) {
                viewModel.updateQuizQuestion(this, quizQuestion);
            } else {

                viewModel.addQuizQuestion(this, quizQuestion);
            }
        });

        if (editQuestion != null) {

            dialogue.setEditQuestion(editQuestion, questionID -> viewModel.deleteQuizQuestion(this, questionID));

        }

        dialogue.show();

    }

    private void showQuestionsRecycler(List<QuizQuestion> quizQuestionList) {

        quizQuestionAdapter = new QuizQuestionAdapter(this, quiz, quizSection, quizQuestionList, obj -> {

            QuizQuestion editQuestion = (QuizQuestion) obj;
            showQuestionDialogue(editQuestion);

        });
        mBinding.RVAllQuizQuestionsRecycler.setAdapter(quizQuestionAdapter);

    }


}