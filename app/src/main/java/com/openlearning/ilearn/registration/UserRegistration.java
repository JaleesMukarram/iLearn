package com.openlearning.ilearn.registration;

import android.util.Log;
import android.util.Patterns;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.openlearning.ilearn.registration.interfaces.AuthStateRequestListener;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;

import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_MEDIUM;
import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_SMALL;
import static com.openlearning.ilearn.utils.CommonUtils.MAX_PASSWORD_LENGTH;
import static com.openlearning.ilearn.utils.CommonUtils.MIN_LENGTH_SMALL;
import static com.openlearning.ilearn.utils.CommonUtils.MIN_PASSWORD_LENGTH;
import static com.openlearning.ilearn.utils.CommonUtils.STRING_EMPTY;
import static com.openlearning.ilearn.utils.CommonUtils.STRING_SPACE;
import static com.openlearning.ilearn.utils.CommonUtils.VALIDATION_SUCCESS;

public class UserRegistration {

    public static final String TAG = "UserRegTAG";


    private static UserRegistration instance;

    private final FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private final UserDatabase mUserDatabase;

    private AuthStateRequestListener totalStateListener;

    private UserRegistration() {

        Log.d(TAG, "UserRegistration: instance started");

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = UserDatabase.getInstance();

        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {

            Log.d(TAG, "UserRegistration: auth state change triggered");
            mUser = firebaseAuth.getCurrentUser();

            if (this.totalStateListener != null) {

                Log.d(TAG, "UserRegistration: stateListener was attached. Sending details");
                sendAuthStates();

            }

        };

        mAuth.addAuthStateListener(authStateListener);

//        mAuth.signOut();


    }

    public static UserRegistration getInstance() {
        if (instance == null) {

            instance = new UserRegistration();

        }
        return instance;
    }

    public void setTotalStateListener(AuthStateRequestListener totalStateListener) {
        this.totalStateListener = totalStateListener;

    }

    // Validations
    public String validateEmail(String email) {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            String status = "Email not valid";

            Log.d(TAG, status);

            return status;
        }
        return VALIDATION_SUCCESS;
    }

    public String validatePassword(String password) {

        Log.d(TAG, "Password validation started");

        if (password == null || password.equals(STRING_EMPTY)) {

            String status = "Password Empty";
            Log.d(TAG, status);
            return status;

        } else if (password.contains(STRING_SPACE)) {

            String status = "Password contains spaces";
            Log.d(TAG, status);
            return status;

        } else if (password.length() < MIN_PASSWORD_LENGTH) {

            String status = "Password less than " + MIN_PASSWORD_LENGTH + " characters";
            Log.d(TAG, status);
            return status;

        } else if (password.length() > MAX_PASSWORD_LENGTH) {

            String status = "Password greater than " + MAX_PASSWORD_LENGTH + " characters";
            Log.d(TAG, status);
            return status;

        }

        return VALIDATION_SUCCESS;
    }

    public String validateName(String name) {

        Log.d(TAG, "Password validation started");

        if (name == null || name.equals(STRING_EMPTY)) {

            String status = "Name Empty";
            Log.d(TAG, status);
            return status;

        } else if (name.length() < MIN_LENGTH_SMALL) {

            String status = "Name less than " + MIN_PASSWORD_LENGTH + " characters";
            Log.d(TAG, status);
            return status;

        } else if (name.length() > MAX_LENGTH_SMALL) {

            String status = "Name greater than " + MAX_PASSWORD_LENGTH + " characters";
            Log.d(TAG, status);
            return status;

        }

        return VALIDATION_SUCCESS;

    }

    // Auth Operations
    public void createNewUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {

                        Log.d(TAG, "Task failed to complete");

                        if (totalStateListener != null) {

                            totalStateListener.onException(task.getException());

                        }

                        return;
                    }

                    Log.d(TAG, "createNewUser: User created");
                });

    }

    public boolean authUserSignedIn() {

        return mUser != null;
    }

    public void signInUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.d(TAG, "signInUser: failed: " + task.getException());
                        if (totalStateListener != null) {

                            totalStateListener.onException(task.getException());

                        }
                        return;
                    }

                    Log.d(TAG, "signInUser: User signed In");


                });


    }

    public String getUserEmail() {

        if (mUser != null) {
            return mUser.getEmail();
        }
        return null;
    }

    public String getUserID() {

        mUser = mAuth.getCurrentUser();

        if (mUser != null) {
            return mUser.getUid();
        }

        return null;
    }


    // Database Operations
    public void insertCurrentUserIntoDatabase(String name, int userType) {

        mUser = mAuth.getCurrentUser();
        if (mUser == null) return;

        User user = new User(name, userType);
        user.setId(mUser.getUid());
        user.setEmail(mUser.getEmail());

        mUserDatabase.insertNewUser(user, true, new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                if (totalStateListener != null) {

                    totalStateListener.onUserCompletelyRegistered(obj);
                }

            }

            @Override
            public void onFailure(Exception ex) {

                if (totalStateListener != null) {
                    totalStateListener.onException(ex);
                }
            }
        });


    }

    public User getCurrentUserFromDB() {

        return mUserDatabase.getCurrentDBUser();
    }

    public void getNameOfThisUser(String id, FirebaseSuccessListener listener) {

        mUserDatabase.getNameOfUserWithId(id, listener);
    }

    // State Operations
    private void sendAuthStates() {

        Log.d(TAG, "sendAuthStates: sending auth state");

        if (authUserSignedIn()) {

            mUserDatabase.getUserWithThisID(mUser.getUid(), true, new FireStoreObjectGetListener() {
                @Override
                public void onSuccess(@Nullable Object obj) {

                    if (obj == null) {

                        Log.d(TAG, "onSuccess: result null. only auth registered");
                        totalStateListener.onFoundButNotRegistered();

                    } else {

                        User user = (User) obj;
                        Log.d(TAG, "onSuccess: " + user.toString());
                        totalStateListener.onUserCompletelyRegistered(user);
                    }

                }

                @Override
                public void onFailure(Exception ex) {

                    totalStateListener.onUserRegistrationCheckError(ex);

                }
            });

        } else {

            totalStateListener.onNoUserSignedIn();
            Log.d(TAG, "sendAuthStates: sent no user signed in");

        }
    }

    public void requestAuthStateFromServerNow() {

        Log.d(TAG, "getAuthStateNow: class want to get auth state");

        if (totalStateListener != null) {
            sendAuthStates();

        }
    }


    public void signOutUser() {

        mAuth.signOut();
    }

    public void changeUserName(String name) {

        mUserDatabase.changeUserNameIntoDatabase(name);
    }

    public void changeUserPassword(String password, FirebaseSuccessListener listener) {

        mUser.updatePassword(password).addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                listener.onFailure(task.getException());
                return;
            }

            listener.onSuccess(null);

        });
    }

}
