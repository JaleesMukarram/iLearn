package com.openlearning.ilearn.quiz.client.dialogues;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewQuizSectionDetailsBinding;
import com.openlearning.ilearn.quiz.client.adapter.QuestionStatusAdapter;
import com.openlearning.ilearn.quiz.client.layers.QuizLogic;
import com.openlearning.ilearn.quiz.client.layers.SectionManager;


public class DialogueSectionDetails {

    private final QuizLogic quizLogic;
    private final SectionManager sectionManager;
    private Dialog mDialog;

    private ViewQuizSectionDetailsBinding mBinding;


    public DialogueSectionDetails(QuizLogic quizCore, SectionManager sectionManager) {
        this.quizLogic = quizCore;
        this.sectionManager = sectionManager;

        this.sectionManager.validateThings();
    }

    @SuppressLint("InflateParams")
    public void initViews() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(sectionManager.getContext());
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(sectionManager.getContext()), R.layout.view_quiz_section_details, null, false);

        mBuilder.setView(mBinding.getRoot());

        mDialog = mBuilder.create();
        mDialog.show();

        setData();

    }

    private void setData() {

        mBinding.TVQuizSectionQuizName.setText(quizLogic.getQuiz().getQuizName());

        mBinding.TVQuizSectionTotalAnsweredShowing.setText(String.valueOf(sectionManager.getTotalAnswered()));
        mBinding.TVQuizSectionTotalNotAnsweredShowing.setText(String.valueOf(sectionManager.getTotalNotAnswered()));

        mBinding.TVQuizSectionTotalNotVisitedShowing.setText(String.valueOf(sectionManager.getTotalNotVisited()));
        mBinding.TVQuizSectionTotalMarkedShowing.setText(String.valueOf(sectionManager.getTotalMarked()));

        mBinding.RVQuizSectionAllQuestionsRecycler.setAdapter(new QuestionStatusAdapter(sectionManager, mDialog));

        if (quizLogic.quizCompleted) {

            mBinding.BTNSubmit.setText(R.string.view_result);
            mBinding.BTNSubmit.setOnClickListener(v -> {

                quizLogic.showResult();
                mDialog.cancel();
            });

        } else {

            mBinding.BTNSubmit.setOnClickListener(v -> {

                quizLogic.onQuizFinish();
                mDialog.cancel();
            });
        }


    }

}
