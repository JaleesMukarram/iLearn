package com.openlearning.ilearn.quiz.client.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;

import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewQuizInfoDialogueBinding;
import com.openlearning.ilearn.quiz.client.layers.QuizLogic;


public class DialogueQuizStart {

    private final QuizLogic logicLayer;
    private Dialog mDialogue;

    public DialogueQuizStart(QuizLogic logicLayer) {
        this.logicLayer = logicLayer;
    }

    public void initAndShow() {

        ViewQuizInfoDialogueBinding mBinding = DataBindingUtil.inflate(logicLayer.getHomeScreen().getLayoutInflater(), R.layout.view_quiz_info_dialogue, null, false);

        mBinding.setQuiz(logicLayer.getQuiz());

        mBinding.BTNAttemptNow.setOnClickListener(v -> {

            logicLayer.checkIfSavedVersionIsAvailable();
            mDialogue.cancel();
        });

        mBinding.BTNLater.setOnClickListener(v -> logicLayer.getHomeScreen().finish());

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(logicLayer.getHomeScreen());
        mBuilder.setView(mBinding.getRoot());

        mDialogue = mBuilder.create();
        mDialogue.show();

    }
}
