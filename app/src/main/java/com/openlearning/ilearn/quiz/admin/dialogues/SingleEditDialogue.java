package com.openlearning.ilearn.quiz.admin.dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewEditDialogueBinding;

public class SingleEditDialogue {

    private AlertDialog.Builder mBuilder;
    private ViewEditDialogueBinding mBinding;
    private String showText;
    private Dialog mDialog;
    private EditComplete editComplete;

    private final Activity homeScreen;

    public SingleEditDialogue(Activity homeScreen) {
        this.homeScreen = homeScreen;
    }

    public void ready(String showText, EditComplete editComplete) {
        this.showText = showText;
        mBinding = DataBindingUtil.inflate(homeScreen.getLayoutInflater(), R.layout.view_edit_dialogue, null, false);
        View mView = mBinding.getRoot();

        mBinding.BTNOK.setOnClickListener(v -> {

            if (mDialog != null) {

                mDialog.cancel();
                editComplete.onEdit(mBinding.ETFieldName.getText().toString());
            }

        });

        mBuilder = new AlertDialog.Builder(homeScreen);
        mBuilder.setView(mView);
    }

    public void show() {

        mBinding.TVAskingText.setText(showText);
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

        void onEdit(String editField);
    }

}
