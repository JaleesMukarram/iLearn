package com.openlearning.ilearn.chat.queries;

import androidx.annotation.NonNull;

import java.util.Date;

public class ImageChat extends Chat implements Cloneable {

    private String imageUri;
    private int chatType = CHAT_TYPE_IMAGE;

    public ImageChat() {
    }

    public ImageChat(String imageUri) {
        this.imageUri = imageUri;
    }

    public ImageChat(String sendingUserID, String receivingUserID, String imageUri) {
        super(sendingUserID, receivingUserID, CHAT_TYPE_IMAGE);
        this.imageUri = imageUri;
    }

    public ImageChat(String sendingUserID, String receivingUserID, Date sentDate, Date receivedDate, Date readDate, String imageUri) {
        super(sendingUserID, receivingUserID, sentDate, readDate);
        this.imageUri = imageUri;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getChatType() {
        return chatType;
    }

}
