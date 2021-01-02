package com.openlearning.ilearn.interfaces;


public interface StorageUploadInterface {

    // Start the operation sequence
    void execute();

    // Start Uploading the firebase database object
    void startUploadingToStorage();

    // When the object has reached the firebase storage
    void onStorageUploadSuccess();

    // When the object has failed to reached the firebase storage
    void onStorageUploadFailed(Exception ex);


    // Start getting the Uri of the uploaded object
    void startFetchingUploadUri();

    // When the object Uri has been fetched
    void onUriFetchingSuccess();

    // When the object Uri has been failed to be fetched
    void onUriFetchingFailed(Exception ex);

    // If the operation has been done successfully
    void onCompleteSuccess();

    // If the operation has been done successfully
    void onCompleteFailure(Exception ex);

    // If the operation has been completely failed
    void removeEverything();

}
