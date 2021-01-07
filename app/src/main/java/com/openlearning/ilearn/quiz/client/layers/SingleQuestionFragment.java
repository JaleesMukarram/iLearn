package com.openlearning.ilearn.quiz.client.layers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.SingleQuizQuestionAttempBinding;
import com.openlearning.ilearn.quiz.admin.modals.QuizQuestion;

import java.util.ArrayList;
import java.util.List;

public class SingleQuestionFragment extends Fragment {


    private static final String TAG = "SingleQFrag";
    private final List<OptionManager> singleOptionList;
    private final QuizQuestion mQuestion;
    private final int questionIndex;
    SectionManager sectionManager;

    private SingleQuizQuestionAttempBinding mBinding;

    private boolean forResult;

    public SingleQuestionFragment(SectionManager sectionManager, QuizQuestion mQuestion, int questionIndex) {
        this.sectionManager = sectionManager;
        this.mQuestion = mQuestion;
        this.questionIndex = questionIndex;

        singleOptionList = new ArrayList<>();

        initOptions();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.single_quiz_question_attemp, container, false);

        callHooks();

        return mBinding.getRoot();
    }


    public void callHooks() {

        initListeners();
        process();

    }

    public void initListeners() {

        mBinding.IVMark.setOnClickListener(v -> mQuestion.setMarked(mBinding.IVMark.isChecked()));

    }

    private void initOptions() {

        for (int i = 0; i < mQuestion.getQuestionOptionList().size(); i++) {

            OptionManager optionManager = new OptionManager(mQuestion.getQuestionOptionList().get(i), i + 1);
            singleOptionList.add(optionManager);

        }
    }

    public void process() {

        showQuizQuestion();
        String questionNumber = "Question: " + (questionIndex + 1);
        mBinding.TVQuestionNumber.setText(questionNumber);
//        if (sectionManager.quizCore.practice) {
//
//            positiveShowing.setVisibility(View.GONE);
//            negativeShowing.setVisibility(View.GONE);
//
//        }
//
//        else {

        String positive = "+ " + mQuestion.getPositive();
        mBinding.TVPositive.setText(positive);

        if (mQuestion.getNegative() == 0) {

            mBinding.TVNegative.setVisibility(View.GONE);

        } else {

            String negative = "- " + mQuestion.getNegative();
            mBinding.TVNegative.setText(negative);

        }
//        }


        showAllQuestionOptions();

        if (forResult) {
            markCorrectQuestion();
            hideBookMark();
//            showExplanation();
        }

//        if (mQuestion.isAnswered() && sectionManager.quizCore.practice) {
//
//            validatePracticeThings();
//            showExplanation();
//        }


    }

    private void showAllQuestionOptions() {

        for (OptionManager optionUtils : singleOptionList) {

            optionUtils.attach(this, mBinding.LLOptionAppender);
        }
    }

    private void showQuizQuestion() {

        mBinding.TVQuestionTitle.setText(mQuestion.getName());
    }

    private void hideBookMark() {

        mBinding.IVMark.setVisibility(View.GONE);

    }

    public void thisOptionSelected(int sequenceNumber) {

        markThisSequencedOptionSelectedOnly(sequenceNumber);

        if (sectionManager.isPractice()) {

            validatePracticeThings();

        } else {

            sectionManager.moveToNextQuestion();
        }
    }

    private void validatePracticeThings() {

//        sectionManager.quizCore.validatePracticeThings();
        markCorrectQuestion();

    }

    // Result
    public void markCorrectQuestion() {

        completeQuiz();
        forResult = true;

        for (OptionManager option : singleOptionList) {

            if (option.getmQuestionOption().isCorrect()) {

                if (mQuestion.isAnswered()) {

                    if (option.isSelected()) {

                        option.selectedRight();

                    } else {

                        findSelectedAndMarkIncorrect();
                        option.markRightOne();
                    }

                } else {

                    option.markRightOne();
                }
            }
        }
    }

    private void completeQuiz() {

        for (OptionManager option : singleOptionList) {

            option.completeQuiz();
        }

//        if (questionExplanation != null) {
//
//            showExplanation();
//
//        }

    }

    private void findSelectedAndMarkIncorrect() {

        for (OptionManager option : singleOptionList) {

            if (option.isSelected()) {

                option.selectedWrong();

            }
        }
    }


    private void markThisSequencedOptionSelectedOnly(int sequenceNumber) {

        for (OptionManager optionUtils : singleOptionList) {

            optionUtils.setSelected(optionUtils.getSequenceNumber() == sequenceNumber);

            optionUtils.validateOption();

        }

        this.mQuestion.setAnswered(true);
    }

    private void markThisSequencedOptionSelectedAlso(int sequenceNumber) {

        int totalSelected = 0;

        for (OptionManager optionUtils : singleOptionList) {

            if (optionUtils.getSequenceNumber() != sequenceNumber) {

                if (optionUtils.isSelected()) {
                    totalSelected++;
                }

            } else {

                if (optionUtils.isSelected()) {
                    optionUtils.setSelected(false);
                } else {
                    optionUtils.setSelected(true);
                    totalSelected++;
                }


            }
        }

        if (totalSelected > 0) {

            this.mQuestion.setAnswered(true);
        } else {

            this.mQuestion.setAnswered(false);
        }

    }

    //

    public QuizQuestion getmQuestion() {
        return mQuestion;
    }

    public double getThisQuestionObtainedMarks() {


        for (OptionManager optionManager : singleOptionList) {

            if (optionManager.isSelected()) {

                if (optionManager.getmQuestionOption().isCorrect()) {

                    Log.d(TAG, "positive: " + mQuestion.getID() + " : " + mQuestion.getPositive());
                    return mQuestion.getPositive();

                } else {

                    Log.d(TAG, "megative: " + mQuestion.getID() + " : " + -mQuestion.getNegative());
                    return -mQuestion.getNegative();

                }
            }
        }

        Log.d(TAG, "not answered: " + mQuestion.getID() + " : " + 0);

        return 0;
    }

    public double getThisQuestionTotalMarks() {

        return mQuestion.getPositive();

    }

    public double getThisQuestionNegativeMarks() {

        if (mQuestion.isAnswered()) {

            if (singleOptionList.size() > 0) {

                for (OptionManager optionUtils : singleOptionList) {

                    if (optionUtils.isSelected()) {

                        if (optionUtils.getmQuestionOption().isCorrect()) {

                            return 0;
                        }
                    }
                }

                return mQuestion.getNegative();
            }


        }

        return 0;
    }

}
