package com.openlearning.ilearn.dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewMsgDialogueBinding;

public class LoadingDialogue {

    private AlertDialog.Builder mBuilder;
    private ViewMsgDialogueBinding mBinding;
    private Dialog mDialog;

    private String shortMessage;
    private String longMessage;
    private final Activity homeScreen;


    public LoadingDialogue(Activity homeScreen) {
        this.homeScreen = homeScreen;
    }

    public void ready(String shortMessage, String longMessage) {

        this.shortMessage = shortMessage;
        this.longMessage = longMessage;
        mBinding = DataBindingUtil.inflate(homeScreen.getLayoutInflater(), R.layout.view_msg_dialogue, null, false);
        View mView = mBinding.getRoot();

        mBuilder = new AlertDialog.Builder(homeScreen);
        mBuilder.setView(mView);
    }

    public void show() {
        mBinding.setLongMessage(longMessage);
        mBinding.setShortMessage(shortMessage);

        mDialog = mBuilder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

    }

    public void cancel() {

        if (mDialog != null) {

            mDialog.cancel();
        }
    }
}
