package com.openlearning.ilearn.view_models;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.dialogues.LoadingDialogue;
import com.openlearning.ilearn.registration.interfaces.AuthStateRequestListener;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.utils.CommonUtils;

import static com.openlearning.ilearn.utils.CommonUtils.VALIDATION_SUCCESS;

public class SignUpVM extends ViewModel {

    private static final String TAG = "SignUpVM";
    private final UserRegistration userRegistration;
    private LoadingDialogue loadingDialogue;
    private final MutableLiveData<Boolean> signUpSuccess;

    public LiveData<Boolean> getSignUpSuccess() {
        return signUpSuccess;
    }

    public SignUpVM() {

        userRegistration = UserRegistration.getInstance();
        signUpSuccess = new MutableLiveData<>();
        signUpSuccess.setValue(false);
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

        Log.d(TAG, "validateUser: User Success");
        createNewUser(activity, email, password);

    }

    private void createNewUser(Activity activity, String email, String password) {

        showLoadingDialogue(activity);
        userRegistration.createNewUser(email, password);
        userRegistration.setTotalStateListener(new AuthStateRequestListener() {

            @Override
            public void onUserCompletelyRegistered(Object obj) {
            }

            @Override
            public void onFoundButNotRegistered() {

                signUpSuccess.setValue(true);
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
        loadingDialogue.ready("Please wait...", "Please wait while we are creating your new account for iLearn");
        loadingDialogue.show();
    }

}
