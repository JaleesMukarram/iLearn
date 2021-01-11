package com.openlearning.ilearn.chat.queries;

import androidx.annotation.NonNull;

import java.util.Date;

public class DocumentChat extends Chat implements Cloneable {

    private final int chatType = CHAT_TYPE_DOCUMENTS;
    private String documentUri;
    private String documentName;
    private long documentSize;
    private int progress = 100;


    public DocumentChat() {
    }

    public DocumentChat(String documentUri, String documentName, long documentSize) {
        this.documentUri = documentUri;
        this.documentName = documentName;
        this.documentSize = documentSize;
    }

    public DocumentChat(String sendingUserID, String receivingUserID, String documentUri, String documentName, long documentSize) {
        super(sendingUserID, receivingUserID, CHAT_TYPE_DOCUMENTS);
        this.documentUri = documentUri;
        this.documentName = documentName;
        this.documentSize = documentSize;
    }

    public DocumentChat(String sendingUserID, String receivingUserID, Date sentDate, Date receivedDate, Date readDate, String documentUri, String documentName) {
        super(sendingUserID, receivingUserID, sentDate, readDate);
        this.documentUri = documentUri;
        this.documentName = documentName;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public long getDocumentSize() {
        return documentSize;
    }

    public void setDocumentSize(long documentSize) {
        this.documentSize = documentSize;
    }

    public String getDocumentUri() {
        return documentUri;
    }

    public void setDocumentUri(String documentUri) {
        this.documentUri = documentUri;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public int getChatType() {
        return chatType;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
