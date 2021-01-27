package com.openlearning.ilearn.quiz.admin.dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewAddQuestionDialogueBinding;
import com.openlearning.ilearn.interfaces.AddedCompleteListener;
import com.openlearning.ilearn.interfaces.ConfirmationListener;
import com.openlearning.ilearn.quiz.admin.modals.QuestionOption;
import com.openlearning.ilearn.quiz.admin.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.admin.utils.OptionAddingUtils;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.UUID;

public class AddQuestionDialogue {

    private static final String TAG = "AddQuestionDlgTAG";
    private AlertDialog.Builder mBuilder;
    private ViewAddQuestionDialogueBinding mBinding;
    private Dialog mDialog;

    private final Activity homeScreen;
    private final OptionAddingUtils optionAddingUtils;
    private QuizQuestion editQuestion;

    public AddQuestionDialogue(Activity homeScreen) {
        this.homeScreen = homeScreen;
        this.optionAddingUtils = new OptionAddingUtils();
    }

    public void ready(AddedCompleteListener addedCompleteListener) {

        mBinding = DataBindingUtil.inflate(homeScreen.getLayoutInflater(), R.layout.view_add_question_dialogue, null, false);
        View mView = mBinding.getRoot();

        mBinding.BTNAddOptionSpace.setOnClickListener(v -> optionAddingUtils.addNewOptionSpace(mBinding.LLQuestionOptions, null));

        mBinding.BTNAdd.setOnClickListener(v -> {

            if (checkQuestion(
                    mBinding.ETQuestionName.getText().toString(),
                    mBinding.ETPositive.getText().toString().trim(),
                    mBinding.ETNegative.getText().toString().trim())) {

                Log.d(TAG, "ready: first question validated");

                if (optionAddingUtils.validateAllOptions(homeScreen)) {

                    if (editQuestion != null) {

                        Toast.makeText(homeScreen, "Editing question...", Toast.LENGTH_SHORT).show();
                        addedCompleteListener.onReadyForAdd(makeQuestion(mBinding.ETQuestionName.getText().toString(),
                                mBinding.ETPositive.getText().toString().trim(),
                                mBinding.ETNegative.getText().toString().trim()), true);


                    } else {

                        Toast.makeText(homeScreen, "Adding question...", Toast.LENGTH_SHORT).show();
                        addedCompleteListener.onReadyForAdd(makeQuestion(mBinding.ETQuestionName.getText().toString(),
                                mBinding.ETPositive.getText().toString().trim(),
                                mBinding.ETNegative.getText().toString().trim()), false);


                    }


                    cancel();
                }
            }
        });

        mBinding.IVCancel.setOnClickListener(v -> cancel());

        mBuilder = new AlertDialog.Builder(homeScreen);
        mBuilder.setView(mView);
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

    public void setEditQuestion(QuizQuestion editQuestion, DeleteListener deleteListener) {
        this.editQuestion = editQuestion;
        Log.d(TAG, "setEditQuestion: edit mode");
        mBinding.setQuestion(editQuestion);
        mBinding.BTNDelete.setVisibility(View.VISIBLE);
        mBinding.BTNDelete.setOnClickListener(v -> {

            CommonUtils.showConfirmationDialogue(homeScreen, "Delete this question?", "Are you sure to delete this question permanently?", "Yes", new ConfirmationListener() {
                @Override
                public void onPositive() {
                    deleteListener.onDelete(editQuestion.getID());
                    cancel();
                }

                @Override
                public void onNegative() {

                }
            });

        });

        for (QuestionOption questionOption : editQuestion.getQuestionOptionList()) {

            optionAddingUtils.addNewOptionSpace(mBinding.LLQuestionOptions, questionOption);
        }
    }

    private QuizQuestion makeQuestion(String questionName, String positive, String negative) {

        if (editQuestion != null) {

            editQuestion.setName(questionName);
            editQuestion.setPositive(Double.parseDouble(positive));
            editQuestion.setNegative(Double.parseDouble(negative));
            editQuestion.setQuestionOptionList(optionAddingUtils.getOptionsList(editQuestion.getID()));

            return editQuestion;

        }

        String questionID = UUID.randomUUID().toString();
        return new QuizQuestion(questionID, questionName, Double.parseDouble(positive), Double.parseDouble(negative), optionAddingUtils.getOptionsList(questionID));
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

            Toast.makeText(homeScreen, "Enter negative marks (without - sign)", Toast.LENGTH_SHORT).show();
            return false;

        }

        double positiveInt = Double.parseDouble(positive);

        if (positiveInt > 10 || positiveInt < 0) {

            Toast.makeText(homeScreen, "Enter positive marks from 0 to 10", Toast.LENGTH_SHORT).show();
            return false;

        }

        double negativeInt = Double.parseDouble(negative);

        if (negativeInt > 10 || negativeInt < 0) {

            Toast.makeText(homeScreen, "Enter negative marks from 0 to 10", Toast.LENGTH_SHORT).show();
            return false;

        }


        return true;

    }

    public interface DeleteListener {

        void onDelete(String questionID);
    }


}
