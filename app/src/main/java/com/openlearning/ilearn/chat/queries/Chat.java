package com.openlearning.ilearn.chat.queries;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@IgnoreExtraProperties
public class Chat implements Cloneable {

    public static final int CHAT_TYPE_MESSAGE = 2;
    public static final int CHAT_TYPE_IMAGE = 4;
    public static final int CHAT_TYPE_DOCUMENTS = 8;

    private String chatID;

    private String sendingUserID;
    private String receivingUserID;

    private int chatType;

    private boolean chatSent = true;

    private Date sentDate;
    private Date readDate;
    private Date typedDate;

    public Chat() {

    }

    public Chat(String sendingUserID, String receivingUserID, int chatType) {

        this.sendingUserID = sendingUserID;
        this.receivingUserID = receivingUserID;
        this.chatType = chatType;

        this.typedDate = new Date();
        this.chatID = UUID.randomUUID().toString();
    }

    public Chat(String sendingUserID, String receivingUserID, Date sentDate, Date readDate) {
        this.sendingUserID = sendingUserID;
        this.receivingUserID = receivingUserID;
        this.sentDate = sentDate;
        this.readDate = readDate;
    }


    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getSendingUserID() {
        return sendingUserID;
    }

    public void setSendingUserID(String sendingUserID) {
        this.sendingUserID = sendingUserID;
    }

    public String getReceivingUserID() {
        return receivingUserID;
    }

    public void setReceivingUserID(String receivingUserID) {
        this.receivingUserID = receivingUserID;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }


    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public Date getTypedDate() {
        return typedDate;
    }

    public void setTypedDate(Date typedDate) {
        this.typedDate = typedDate;
    }

    @Exclude
    public boolean isChatSent() {
        return chatSent;
    }

    public void setChatSent(boolean chatSent) {
        this.chatSent = chatSent;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chatID='" + chatID + '\'' +
                ", sendingUserID='" + sendingUserID + '\'' +
                ", receivingUserID='" + receivingUserID + '\'' +
                ", chatType=" + chatType +
                ", chatSent=" + chatSent +
                ", sentDate=" + sentDate +
                ", readDate=" + readDate +
                ", typedDate=" + typedDate +
                '}';
    }
}
