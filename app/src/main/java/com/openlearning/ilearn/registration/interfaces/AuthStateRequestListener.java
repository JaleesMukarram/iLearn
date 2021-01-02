package com.openlearning.ilearn.registration.interfaces;

public interface AuthStateRequestListener {

    /**
     * When a user is signed in and is fully registered
     *
     * @param obj is the user object returned
     */

    void onUserCompletelyRegistered(Object obj);


    /**
     * When the user is signed IN but following conditions happens
     * 1. When the user in not registered
     * 2. When the user is not valid because of its fields values are missing
     */
    void onFoundButNotRegistered();


    /**
     * When the user is signed IN but error happens while checking his
     * status on database server. Or request timeout occurs
     *
     * @param ex will be the exception
     */
    void onUserRegistrationCheckError(Exception ex);


    /**
     * When no signed in user is found on Client Device,
     */
    void onNoUserSignedIn();


    /**
     * This method will get the exception being thrown
     *
     * @param ex The exception thrown
     */
    void onException(Exception ex);

}
