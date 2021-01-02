package com.openlearning.ilearn.view_models;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.registration.interfaces.AuthStateRequestListener;
import com.openlearning.ilearn.registration.User;

import static com.openlearning.ilearn.registration.User.ACCOUNT_HOLDED;
import static com.openlearning.ilearn.registration.User.STATUS_COMPLETE_REGISTERED;
import static com.openlearning.ilearn.registration.User.STATUS_HALF_REGISTERED;
import static com.openlearning.ilearn.registration.User.STATUS_NOT_REGISTERED;
import static com.openlearning.ilearn.registration.User.STATUS_UNKNOWN_REGISTERED;

public class SplashVM extends ViewModel {

    private static final String TAG = "SplashVMTAG";
    private final UserRegistration userRegistration;
    private final MutableLiveData<Integer> userStatus;
    private User user;


    public SplashVM() {

        userRegistration = UserRegistration.getInstance();
        userStatus = new MutableLiveData<>();
    }

    public void checkRegistrationStatus() {

        userRegistration.setTotalStateListener(new AuthStateRequestListener() {

            @Override
            public void onUserCompletelyRegistered(Object obj) {

                Log.d(TAG, "onUserCompletelyRegistered: " + obj);

                user = (User) obj;

                if (user.getAccountStatus() == User.ACCOUNT_OK) {

                    userStatus.setValue(STATUS_COMPLETE_REGISTERED);
                    Log.d(TAG, "onUserCompletelyRegistered: account OK");

                } else {

                    userStatus.setValue(ACCOUNT_HOLDED);
                    Log.d(TAG, "onUserCompletelyRegistered: account Holded");
                }
            }

            @Override
            public void onFoundButNotRegistered() {

                Log.d(TAG, "onFoundButNotRegistered: ");
                userStatus.setValue(STATUS_HALF_REGISTERED);
            }

            @Override
            public void onUserRegistrationCheckError(Exception ex) {

                Log.d(TAG, "onUserRegistrationCheckError: ");
                userStatus.setValue(STATUS_UNKNOWN_REGISTERED);

            }

            @Override
            public void onNoUserSignedIn() {

                Log.d(TAG, "onNoUserSignedIn: ");
                userStatus.setValue(STATUS_NOT_REGISTERED);

            }

            @Override
            public void onException(Exception ex) {

                Log.d(TAG, "onException: ");
                userStatus.setValue(STATUS_UNKNOWN_REGISTERED);

            }
        });

    }

    public MutableLiveData<Integer> getUserStatus() {
        return userStatus;
    }

    public User getUser() {
        return user;
    }

    public boolean isUserSignedIn(){

        return userRegistration.getUserID() != null;


    }
}


