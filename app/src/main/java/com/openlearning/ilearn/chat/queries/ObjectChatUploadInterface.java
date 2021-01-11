package com.openlearning.ilearn.chat.queries;

public interface ObjectChatUploadInterface {

    // Image handling
    void uploadObjectToStorage();

    void getObjectURI();

    void onObjectUploadSuccess();

    void onObjectUploadFailed();


    //
    void uploadObjectChatToDatabase();

    void onObjectChatUploadSuccess();

    void onObjectChatUploadFailed();

}
