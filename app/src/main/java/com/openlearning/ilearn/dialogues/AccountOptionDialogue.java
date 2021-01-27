package com.openlearning.ilearn.dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.activities.SignIn;
import com.openlearning.ilearn.databinding.ViewAccountOptionDialogueBinding;
import com.openlearning.ilearn.quiz.admin.dialogues.SingleEditDialogue;
import com.openlearning.ilearn.registration.User;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.utils.CommonUtils;

import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_SMALL;
import static com.openlearning.ilearn.utils.CommonUtils.MAX_PASSWORD_LENGTH;
import static com.openlearning.ilearn.utils.CommonUtils.MIN_LENGTH_SMALL;
import static com.openlearning.ilearn.utils.CommonUtils.MIN_PASSWORD_LENGTH;
import static com.openlearning.ilearn.utils.CommonUtils.STRING_EMPTY;
import static com.openlearning.ilearn.utils.CommonUtils.STRING_SPACE;

public class AccountOptionDialogue {

    private static final String TAG = "AccOptDialogTAG";
    private AlertDialog.Builder mBuilder;
    private ViewAccountOptionDialogueBinding mBinding;


    private final UserRegistration userRegistration;
    private final Activity homeScreen;
    private final User user;

    public AccountOptionDialogue(Activity homeScreen, User user) {
        this.homeScreen = homeScreen;
        this.user = user;

        this.userRegistration = UserRegistration.getInstance();
    }

    public void ready() {

        mBinding = DataBindingUtil.inflate(homeScreen.getLayoutInflater(), R.layout.view_account_option_dialogue, null, false);
        View mView = mBinding.getRoot();

        if (user.getAccountType() == User.TYPE_GENERAL_USER) {

            mBinding.IVAccountTypeIcon.setImageDrawable(ContextCompat.getDrawable(homeScreen, R.drawable.ic_reading));
        }
        if (user.getAccountType() == User.TYPE_INSTRUCTOR) {

            mBinding.IVAccountTypeIcon.setImageDrawable(ContextCompat.getDrawable(homeScreen, R.drawable.ic_instructor));

        }

        mBinding.BTNSignOut.setOnClickListener(v -> {
            userRegistration.signOutUser(homeScreen);
            Toast.makeText(homeScreen, user.getName() + " signed out", Toast.LENGTH_SHORT).show();
            CommonUtils.changeActivity(homeScreen, SignIn.class, true);
        });
        mBinding.BTNChangeName.setOnClickListener(v -> startNameChange());
        mBinding.BTNChangePassword.setOnClickListener(v -> startPasswordChange());

        mBuilder = new AlertDialog.Builder(homeScreen);
        mBuilder.setView(mView);
    }

    private void startNameChange() {

        SingleEditDialogue dialogue = new SingleEditDialogue(homeScreen);
        dialogue.ready("Please enter your new name", name -> {

            if (name == null || name.equals(STRING_EMPTY)) {

                String status = "Name Empty";
                CommonUtils.showWarningDialogue(homeScreen, status);
                Log.d(TAG, status);
                return;

            } else if (name.length() < MIN_LENGTH_SMALL) {

                String status = "Name less than " + MIN_LENGTH_SMALL + " characters";
                CommonUtils.showWarningDialogue(homeScreen, status);
                Log.d(TAG, status);
                return;

            } else if (name.length() > MAX_LENGTH_SMALL) {

                String status = "Name greater than " + MAX_LENGTH_SMALL + " characters";
                CommonUtils.showWarningDialogue(homeScreen, status);
                Log.d(TAG, status);
                return;
            }

            userRegistration.changeUserName(name);


        });
        dialogue.show();

    }

    private void startPasswordChange() {

        SingleEditDialogue dialogue = new SingleEditDialogue(homeScreen);
        dialogue.ready("Please enter your new password", password -> {

            if (password == null || password.equals(STRING_EMPTY)) {

                String status = "Password Empty";
                CommonUtils.showWarningDialogue(homeScreen, status);
                Log.d(TAG, status);
                return;

            } else if (password.contains(STRING_SPACE)) {

                String status = "Password contains spaces";
                CommonUtils.showWarningDialogue(homeScreen, status);
                Log.d(TAG, status);
                return;

            } else if (password.length() < MIN_PASSWORD_LENGTH) {

                String status = "Password less than " + MIN_PASSWORD_LENGTH + " characters";
                CommonUtils.showWarningDialogue(homeScreen, status);
                Log.d(TAG, status);
                return;

            } else if (password.length() > MAX_PASSWORD_LENGTH) {

                String status = "Password greater than " + MAX_PASSWORD_LENGTH + " characters";
                CommonUtils.showWarningDialogue(homeScreen, status);
                Log.d(TAG, status);
                return;

            }


            userRegistration.changeUserPassword(password, new FirebaseSuccessListener() {

                @Override
                public void onSuccess(Object obj) {

                    CommonUtils.showSuccessDialogue(homeScreen, "Password Updated");

                }

                @Override
                public void onFailure(Exception ex) {

                    CommonUtils.showWarningDialogue(homeScreen, ex.getLocalizedMessage());

                }
            });


        });
        dialogue.show();

    }

    public void show() {

        mBinding.setUser(user);
        Dialog mDialog = mBuilder.create();
        mDialog.show();

    }
}
