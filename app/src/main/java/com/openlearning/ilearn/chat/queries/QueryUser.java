package com.openlearning.ilearn.chat.queries;

import java.util.Date;

public class QueryUser {

    private String UserID;
    private String name;
    private int totalMessagesCount;
    private int unreadMessagesCount;
    private Date lastMessageDate;

    public QueryUser() {
    }

    public QueryUser(String userID, Date lastMessageDate) {
        UserID = userID;
        this.lastMessageDate = lastMessageDate;
        this.totalMessagesCount = 1;

    }


    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalMessagesCount() {
        return totalMessagesCount;
    }

    public void setTotalMessagesCount(int totalMessagesCount) {
        this.totalMessagesCount = totalMessagesCount;
    }

    public int getUnreadMessagesCount() {
        return unreadMessagesCount;
    }

    public void setUnreadMessagesCount(int unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public void incrementTotalMessages() {

        this.totalMessagesCount++;
    }

    public void incrementUnReadMessages() {

        this.unreadMessagesCount++;
    }

    public boolean unreadMessagesAvailable() {

        return unreadMessagesCount > 0;
    }

}