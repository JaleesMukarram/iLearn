package com.openlearning.ilearn.utils;


import android.net.Uri;

import com.google.firebase.storage.StorageReference;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.interfaces.StorageUploadInterface;
import com.openlearning.ilearn.modals.StorageDocument;
import com.openlearning.ilearn.modals.StorageImage;
import com.openlearning.ilearn.modals.StorageItem;


public class StorageUploadTask implements StorageUploadInterface {

    public static final String TAG = "StorageUpTAG";

    private final StorageReference reference;
    private final String fileName;
    private final Uri uri;
    private final Class<?> returningClass;

    private Uri uploadedUri;

    private FirebaseSuccessListener listener;

    public StorageUploadTask(StorageReference reference, String fileName, Uri uri, Class<?> returningClass) {

        this.reference = reference;
        this.fileName = fileName;
        this.uri = uri;
        this.returningClass = returningClass;
    }

    public void setListener(FirebaseSuccessListener listener) {
        this.listener = listener;
        execute();
    }

    @Override
    public void execute() {

        startUploadingToStorage();

    }

    @Override
    public void startUploadingToStorage() {

        reference.child(fileName)
                .putFile(uri)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        onStorageUploadFailed(task.getException());
                        return;
                    }

                    onStorageUploadSuccess();
                });
    }

    @Override
    public void onStorageUploadSuccess() {

        startFetchingUploadUri();

    }

    @Override
    public void onStorageUploadFailed(Exception ex) {

        onCompleteFailure(ex);
    }

    @Override
    public void startFetchingUploadUri() {

        reference.child(fileName)
                .getDownloadUrl()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        onUriFetchingFailed(task.getException());
                        return;
                    }

                    uploadedUri = task.getResult();
                    onUriFetchingSuccess();

                });

    }

    @Override
    public void onUriFetchingSuccess() {

        onCompleteSuccess();

    }

    @Override
    public void onUriFetchingFailed(Exception ex) {

        onCompleteFailure(ex);

    }

    @Override
    public void onCompleteSuccess() {

        if (returningClass == StorageImage.class) {

            StorageImage storageImage = new StorageImage(fileName, uploadedUri.toString(), reference.getPath());
            listener.onSuccess(storageImage);

        } else if (returningClass == StorageDocument.class) {

            StorageDocument storageDocument = new StorageDocument(fileName, uploadedUri.toString(), reference.getPath());
            listener.onSuccess(storageDocument);

        }
    }

    @Override
    public void onCompleteFailure(Exception ex) {

        listener.onFailure(ex);
        removeEverything();

    }

    @Override
    public void removeEverything() {

        try {

            reference.child(fileName)
                    .delete();

        } catch (Exception ignored) {


        }

    }

}
