package com.openlearning.ilearn.view_models;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.activities.CompleteRegistration;
import com.openlearning.ilearn.dialogues.LoadingDialogue;
import com.openlearning.ilearn.registration.User;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.registration.interfaces.AuthStateRequestListener;
import com.openlearning.ilearn.utils.CommonUtils;

import static com.openlearning.ilearn.utils.CommonUtils.VALIDATION_SUCCESS;

public class SignInVM extends ViewModel {

    private static final String TAG = "SignINVM";
    private final UserRegistration userRegistration;
    private LoadingDialogue loadingDialogue;
    private User user;


    public SignInVM() {
        userRegistration = UserRegistration.getInstance();
    }

    public void validateUser(Activity activity, String email, String password) {

        String emailStatus = userRegistration.validateEmail(email);
        String passwordStatus = userRegistration.validatePassword(password);

        if (!emailStatus.equals(VALIDATION_SUCCESS)) {

            Log.d(TAG, "validateUser: email error: " + emailStatus);
            CommonUtils.showWarningDialogue(activity, emailStatus);
            return;

        }

        if (!passwordStatus.equals(VALIDATION_SUCCESS)) {

            Log.d(TAG, "validateUser: password error: " + passwordStatus);
            CommonUtils.showWarningDialogue(activity, passwordStatus);
            return;

        }

        Log.d(TAG, "validateUser: User validation Success");
        signInThisUser(activity, email, password);

    }

    private void signInThisUser(Activity activity, String email, String password) {

        showLoadingDialogue(activity);
        userRegistration.signInUser(email, password);
        userRegistration.setTotalStateListener(new AuthStateRequestListener() {

            @Override
            public void onUserCompletelyRegistered(Object obj) {

                loadingDialogue.cancel();
                user = (User) obj;
                if (user.getAccountStatus() == User.ACCOUNT_OK) {

                    CommonUtils.changeActivity(activity, CommonUtils.getHomeScreenClass(activity), true);

                } else {

                    CommonUtils.showDangerDialogue(activity, "Your account has been suspended by the administration. Please retry login tomorrow");
                    Log.d(TAG, "onUserCompletelyRegistered: account Holded");
                }

            }

            @Override
            public void onFoundButNotRegistered() {

                CommonUtils.changeActivity(activity, CompleteRegistration.class, true);
                loadingDialogue.cancel();

            }

            @Override
            public void onUserRegistrationCheckError(Exception ex) {
                loadingDialogue.cancel();
                CommonUtils.showWarningDialogue(activity, ex.getLocalizedMessage());
            }

            @Override
            public void onNoUserSignedIn() {

                loadingDialogue.cancel();

            }

            @Override
            public void onException(Exception ex) {
                loadingDialogue.cancel();
                CommonUtils.showWarningDialogue(activity, ex.getLocalizedMessage());

            }
        });


    }

    private void showLoadingDialogue(Activity activity) {
        loadingDialogue = new LoadingDialogue(activity);
        loadingDialogue.ready("Please wait...", "Please wait while we signing in to your account");
        loadingDialogue.show();
    }

}
