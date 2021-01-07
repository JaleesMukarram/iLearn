package com.openlearning.ilearn.quiz.client.dialogues;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSectionResultBinding;
import com.openlearning.ilearn.quiz.client.layers.QuizLogic;
import com.openlearning.ilearn.quiz.client.layers.SectionManager;


public class DialgueSectionResult {

    private static final String TAG = "DSecResTAG";
    private final QuizLogic quizLogic;
    private final SectionManager manager;
    private final int currentSectionIndex;
    ViewSectionResultBinding mBinding;


    public DialgueSectionResult(SectionManager sectionManager, int currentSectionIndex) {
        this.quizLogic = sectionManager.getLogicLayer();
        this.manager = sectionManager;
        this.currentSectionIndex = currentSectionIndex;
        manager.validateThings();

    }

    public void initializeViews() {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(quizLogic.getHomeScreen()), R.layout.view_section_result, null, false);

        setData();

    }


    void addToThisView(LinearLayout linearLayout) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 20, 100, 20);
        linearLayout.addView(mBinding.getRoot(), params);

    }

    private void setData() {


        mBinding.TVSectionNameShowing.setText(quizLogic.getQuiz().getQuizSectionList().get(currentSectionIndex).getName());
        mBinding.TVSectionTotalShowing.setText(String.valueOf(manager.getTotalQuestionOfThisSection(currentSectionIndex)));
        mBinding.TVSectionAttemptedShowing.setText(String.valueOf(manager.getTotalAnsweredOfThisSection(currentSectionIndex)));
        mBinding.TVSectionCorrectShowing.setText(String.valueOf(manager.getTotalSectionCorrectCount(currentSectionIndex)));
        mBinding.TVSectionInCorrectShowing.setText(String.valueOf(manager.getTotalSectionInCorrectCount(currentSectionIndex)));
        mBinding.TVSectionSectionAccuracyShowing.setText(String.valueOf(manager.getAccuracyOfThisSection(currentSectionIndex)));

    }

}
