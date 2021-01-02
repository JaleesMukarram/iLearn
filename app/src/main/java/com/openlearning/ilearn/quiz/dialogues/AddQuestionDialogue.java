package com.openlearning.ilearn.quiz.dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewAddQuestionDialogueBinding;
import com.openlearning.ilearn.quiz.modals.QuestionOption;
import com.openlearning.ilearn.quiz.modals.Quiz;
import com.openlearning.ilearn.quiz.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.utils.OptionAddingUtils;

public class AddQuestionDialogue {

    private static final String TAG = "AddQTAG";
    private AlertDialog.Builder mBuilder;
    private ViewAddQuestionDialogueBinding mBinding;
    private Dialog mDialog;
    private EditComplete editComplete;

    private final Activity homeScreen;
    private final OptionAddingUtils optionAddingUtils;

    public AddQuestionDialogue(Activity homeScreen, Quiz quiz) {
        this.homeScreen = homeScreen;
        this.optionAddingUtils = new OptionAddingUtils(quiz);
    }

    public void ready(EditComplete editComplete) {
        mBinding = DataBindingUtil.inflate(homeScreen.getLayoutInflater(), R.layout.view_add_question_dialogue, null, false);
        View mView = mBinding.getRoot();

        mBinding.BTNAddOptionSpace.setOnClickListener(v -> {

            optionAddingUtils.addNewOptionSpace(mBinding.LLQuestionOptions);
        });

        mBinding.BTNOK.setOnClickListener(v -> {

            if (checkQuestion(mBinding.ETQuestionName.getText().toString(), mBinding.ETPositive.getText().toString().trim(), mBinding.ETNegative.getText().toString().trim())) {

                Log.d(TAG, "ready: first question validated");

                if (optionAddingUtils.checkOptions(homeScreen)) {

                    Toast.makeText(homeScreen, "Adding question...", Toast.LENGTH_SHORT).show();
                    editComplete.onEdit(makeQuestion(mBinding.ETQuestionName.getText().toString(), mBinding.ETPositive.getText().toString().trim(), mBinding.ETNegative.getText().toString().trim()));
                    cancel();

                }
            }
        });

        mBuilder = new AlertDialog.Builder(homeScreen);
        mBuilder.setView(mView);
    }

    private QuizQuestion makeQuestion(String questionName, String positive, String negative) {

        return new QuizQuestion(questionName, Integer.parseInt(positive), Integer.parseInt(negative), optionAddingUtils.getOptionsList());
    }

    public void show() {

        mDialog = mBuilder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

    }

    public void cancel() {

        if (mDialog != null) {

            mDialog.cancel();
        }
    }

    public interface EditComplete {

        void onEdit(QuizQuestion question);

    }

    private boolean checkQuestion(String questionName, String positive, String negative) {

        if (questionName.length() <= 3) {

            Toast.makeText(homeScreen, "Question too short", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (positive.length() <= 0) {

            Toast.makeText(homeScreen, "Enter positive marks", Toast.LENGTH_SHORT).show();
            return false;

        }

        if (negative.length() <= 0) {

            Toast.makeText(homeScreen, "Enter negative marks", Toast.LENGTH_SHORT).show();
            return false;

        }


        int positiveInt = Integer.parseInt(positive);

        if (positiveInt > 10 || positiveInt < 0) {

            Toast.makeText(homeScreen, "Enter positive marks from 0 to 10", Toast.LENGTH_SHORT).show();
            return false;

        }

        int negativeInt = Integer.parseInt(negative);

        if (negativeInt > 10 || negativeInt < 0) {

            Toast.makeText(homeScreen, "Enter negative marks from 0 to 10", Toast.LENGTH_SHORT).show();
            return false;

        }


        return true;

    }

}
