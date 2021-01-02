package com.openlearning.ilearn.utils;


import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.interfaces.StorageUploadInterface;
import com.openlearning.ilearn.modals.StorageImage;


public class StorageUploadTask implements StorageUploadInterface {

    public static final String TAG = "StorageUpTAG";

    private StorageReference reference;
    private String fileName;
    private Uri uri;

    private Uri uploadedUri;

    private FirebaseSuccessListener listener;

    public StorageUploadTask(StorageReference reference, String fileName, Uri uri) {

        this.reference = reference;
        this.fileName = fileName;
        this.uri = uri;
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

        StorageImage storageImage = new StorageImage(fileName, uploadedUri.toString(), reference.getPath());
        listener.onSuccess(storageImage);
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
