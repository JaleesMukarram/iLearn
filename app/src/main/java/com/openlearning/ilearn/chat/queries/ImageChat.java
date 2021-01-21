package com.openlearning.ilearn.chat.queries;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.openlearning.ilearn.modals.StorageImage;


@IgnoreExtraProperties
public class ImageChat extends Chat implements Cloneable {

    private StorageImage storageImage;
    private String imageLocalUri;



    public ImageChat() {
    }

    public ImageChat(String sendingUserID, String receivingUserID, StorageImage storageImage) {
        super(sendingUserID, receivingUserID, CHAT_TYPE_IMAGE);
        this.storageImage = storageImage;
    }

    public ImageChat(String sendingUserID, String receivingUserID, String imageLocalUri) {
        super(sendingUserID, receivingUserID, CHAT_TYPE_IMAGE);
        this.imageLocalUri = imageLocalUri;
    }


    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setStorageImage(StorageImage storageImage) {
        this.storageImage = storageImage;
    }

    public StorageImage getStorageImage() {
        return storageImage;
    }

    @Exclude
    public String getImageLocalUri() {
        return imageLocalUri;
    }

}
