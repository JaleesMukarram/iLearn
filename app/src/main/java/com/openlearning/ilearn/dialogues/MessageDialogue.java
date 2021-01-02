package com.openlearning.ilearn.dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewErrorDialogueBinding;
import com.openlearning.ilearn.databinding.ViewMsgDialogueBinding;

public class MessageDialogue {

    public static final int MODE_WARNING = 1;
    public static final int MODE_DANGER = 2;
    public static final int MODE_SUCCESS = 4;

    private AlertDialog.Builder mBuilder;
    private ViewErrorDialogueBinding mBinding;
    private Dialog mDialog;

    private String message;
    private int currentMode;
    private final Activity homeScreen;

    public MessageDialogue(Activity homeScreen) {
        this.homeScreen = homeScreen;
    }

    public void ready(String message, int mode) {

        this.message = message;
        this.currentMode = mode;
        mBinding = DataBindingUtil.inflate(homeScreen.getLayoutInflater(), R.layout.view_error_dialogue, null, false);
        View mView = mBinding.getRoot();

        mBinding.BTNOK.setOnClickListener(v -> {
            if (mDialog != null) {
                mDialog.cancel();
            }
        });

        mBuilder = new AlertDialog.Builder(homeScreen);
        mBuilder.setView(mView);
    }

    public void show() {

        if (currentMode == MODE_WARNING) {

            mBinding.IVErrorIcon.setImageDrawable(ContextCompat.getDrawable(homeScreen, R.drawable.ic_warning));

        } else if (currentMode == MODE_SUCCESS) {

            mBinding.IVErrorIcon.setImageDrawable(ContextCompat.getDrawable(homeScreen, R.drawable.ic_checked));

        } else {

            mBinding.IVErrorIcon.setImageDrawable(ContextCompat.getDrawable(homeScreen, R.drawable.ic_close));

        }

        mBinding.setMessage(message);
        mDialog = mBuilder.create();
        mDialog.show();

    }
}
