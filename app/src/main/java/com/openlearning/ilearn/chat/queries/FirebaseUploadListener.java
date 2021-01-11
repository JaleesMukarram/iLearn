package com.openlearning.ilearn.chat.queries;

public interface FirebaseUploadListener {

    void onTaskUploadSuccess();

    void onTaskUploadFailed();

    void progress(int progress);
}
