package com.openlearning.ilearn.interfaces;

public interface FirebaseSuccessListener {

    /**
     * The success of any operation will be called
     *
     * @param obj This will be the object returned by the database
     */
    void onSuccess(Object obj);

    /**
     * The failure of any operation will be called
     *
     * @param ex This will be the exception caused in the firebase
     */
    void onFailure(Exception ex);
}
