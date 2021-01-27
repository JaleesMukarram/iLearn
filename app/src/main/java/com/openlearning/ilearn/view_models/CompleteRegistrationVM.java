package com.openlearning.ilearn.view_models;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.activities.HomeScreen;
import com.openlearning.ilearn.activities.HomeScreenInstructor;
import com.openlearning.ilearn.dialogues.LoadingDialogue;
import com.openlearning.ilearn.registration.User;
import com.openlearning.ilearn.registration.interfaces.AuthStateRequestListener;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.utils.CommonUtils;

import static com.openlearning.ilearn.utils.CommonUtils.VALIDATION_SUCCESS;

public class CompleteRegistrationVM extends ViewModel {

    private static final String TAG = "ComRegVM";
    private final UserRegistration userRegistration;
    private int userType;

    public CompleteRegistrationVM() {
        this.userRegistration = UserRegistration.getInstance();
    }

    public void validateName(Activity activity, String name) {

        String nameStatus = userRegistration.validateName(name);

        if (!nameStatus.equals(VALIDATION_SUCCESS)) {

            CommonUtils.showWarningDialogue(activity, nameStatus);
            return;
        }

        LoadingDialogue loadingDialogue = CommonUtils.showLoadingDialogue(activity, "Please wait...", "Please wait while we save your account details");
        userRegistration.insertCurrentUserIntoDatabase(name, userType);
        userRegistration.setTotalStateListener(new AuthStateRequestListener() {
            @Override
            public void onUserCompletelyRegistered(Object obj) {

                Log.d(TAG, "onUserCompletelyRegistered: ");
                loadingDialogue.cancel();

                User user = (User) obj;
                if (user.getAccountStatus() == User.ACCOUNT_OK) {

                    CommonUtils.changeActivity(activity, CommonUtils.getHomeScreenClass(activity), true);

                } else {

                    CommonUtils.showDangerDialogue(activity, "Your account has been suspended by the administration. Please retry login tomorrow");
                }

            }

            @Override
            public void onFoundButNotRegistered() {

                loadingDialogue.cancel();

            }

            @Override
            public void onUserRegistrationCheckError(Exception ex) {

                Log.d(TAG, "onUserCompletelyRegistered: ");
                loadingDialogue.cancel();
                CommonUtils.showWarningDialogue(activity, ex.getLocalizedMessage());

            }

            @Override
            public void onNoUserSignedIn() {

                loadingDialogue.cancel();


            }

            @Override
            public void onException(Exception ex) {
                Log.d(TAG, "onUserCompletelyRegistered: ");
                CommonUtils.showWarningDialogue(activity, ex.getLocalizedMessage());
                loadingDialogue.cancel();

            }
        });

    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUserEmail() {

        return userRegistration.getUserEmail();
    }

}
