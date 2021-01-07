package com.openlearning.ilearn.quiz.client.layers;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSingleOptionBinding;
import com.openlearning.ilearn.quiz.admin.modals.QuestionOption;


public class OptionManager {

    private final QuestionOption mQuestionOption;
    private final int sequenceNumber;

    private SingleQuestionFragment questionFragment;

    private ViewSingleOptionBinding mBinding;

    public OptionManager(QuestionOption mQuestionOption, int sequenceNumber) {
        this.mQuestionOption = mQuestionOption;
        this.sequenceNumber = sequenceNumber;
    }

    void attach(SingleQuestionFragment questionFragment, LinearLayout optionsAppender) {

        this.questionFragment = questionFragment;

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(optionsAppender.getContext()), R.layout.view_single_option, null, false);
        optionsAppender.addView(mBinding.getRoot());

        initListeners();
        setViews();

        validateOption();

    }

    private void setViews() {

        mBinding.TVOptionNumberShowing.setText(getOptionNumber());
        mBinding.TVOptionShowing.setText(mQuestionOption.getTitle());

    }

    private void initListeners() {

        mBinding.CVMainContainer.setOnClickListener(view -> questionFragment.thisOptionSelected(sequenceNumber));

    }

    private String getOptionNumber() {

        switch (sequenceNumber) {

            case 1:
                return "A";

            case 2:
                return "B";

            case 3:
                return "C";
            case 4:
                return "D";

            case 5:
                return "E";

            case 6:
                return "F";

            case 7:
                return "G";

            case 8:
                return "H";

            default:
                return "I";


        }
    }

    void setThisOptionAsSelected() {

        mQuestionOption.setSelected(true);
        mBinding.CVMainContainer.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.colorPurpleLightMax));
    }

    void setThisOptionAsUnSelected() {

        mQuestionOption.setSelected(false);
        mBinding.CVMainContainer.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.colorWhite));

    }

    void validateOption() {

        if (mQuestionOption.isSelected()) {
            setThisOptionAsSelected();
        } else {
            setThisOptionAsUnSelected();
        }

//        if (selectedRight) {
//
//
//            selectedRight();
//        } else if (selectedWrong) {
//
//            selectedWrong();
//
//        } else if (markRight) {
//
//            markRightOne();
//        }

    }

    void completeQuiz() {

        if (mBinding != null) {

            mBinding.CVMainContainer.setOnClickListener(null);
        }
    }

    void selectedRight() {

        if (mBinding != null) {

            mBinding.CVMainContainer.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.colorGreenFancy));

            mBinding.IVRightOrWrongIcon.setVisibility(View.VISIBLE);
            mBinding.IVRightOrWrongIcon.setImageDrawable(ContextCompat.getDrawable(mBinding.getRoot().getContext(), R.drawable.ic_check_24));

        }

    }

    void selectedWrong() {

        if (mBinding != null) {

            mBinding.CVMainContainer.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.colorRedExtraLight));
            mBinding.IVRightOrWrongIcon.setVisibility(View.VISIBLE);
            mBinding.IVRightOrWrongIcon.setImageDrawable(ContextCompat.getDrawable(mBinding.getRoot().getContext(), R.drawable.ic_close_24));

        }
    }

    void markRightOne() {

        if (mBinding != null) {

            mBinding.CVMainContainer.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.colorGreenFancy));

        }
    }

    public boolean isSelected() {
        return mQuestionOption.isSelected();
    }

    public void setSelected(boolean selected) {
        this.mQuestionOption.setSelected(selected);
    }

    QuestionOption getmQuestionOption() {
        return mQuestionOption;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }
}
