package com.openlearning.ilearn.chat.queries;

import java.util.Date;

public class MessageChat extends Chat {

    private String message;

    public MessageChat() {
    }

    public MessageChat(String sendingUserID, String receivingUserID, String message) {
        super(sendingUserID, receivingUserID, CHAT_TYPE_MESSAGE);
        this.message = message;
    }

    public MessageChat(String sendingUserID, String receivingUserID, Date sentDate, Date receivedDate, Date readDate, String message) {
        super(sendingUserID, receivingUserID, sentDate, readDate);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageChat{" +
                "message='" + message + '\'' +
                '}';
    }
}
