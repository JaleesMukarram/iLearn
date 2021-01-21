package com.openlearning.ilearn.chat.queries;

import androidx.annotation.NonNull;

import com.openlearning.ilearn.modals.StorageDocument;


public class DocumentChat extends Chat implements Cloneable {

    private StorageDocument storageDocument;

    public DocumentChat() {
    }

    public DocumentChat(String sendingUserID, String receivingUserID, StorageDocument storageDocument) {
        super(sendingUserID, receivingUserID, CHAT_TYPE_DOCUMENTS);
        this.storageDocument = storageDocument;
    }



    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public StorageDocument getStorageDocument() {
        return storageDocument;
    }

    public void setStorageDocument(StorageDocument storageDocument) {
        this.storageDocument = storageDocument;
    }

}
