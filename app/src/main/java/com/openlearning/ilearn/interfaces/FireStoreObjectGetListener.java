package com.openlearning.ilearn.interfaces;

import androidx.annotation.Nullable;

public interface FireStoreObjectGetListener {

    /**
     * This method will be fired when the query from FireStore is succeeded
     *
     * @param obj FireStore object from Listener. May be one of the tow
     **/
    void onSuccess(@Nullable Object obj);

    /**
     * This method will be fired when the query from FireStore is succeeded
     *
     * @param ex Exception while getting document from FireStore
     **/
    void onFailure(Exception ex);

}
