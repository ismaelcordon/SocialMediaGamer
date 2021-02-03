package com.icdominguez.socialmediagamer.models;

public class Message {
    private String messageId;
    private String idSender;
    private String idReceiver;
    private String chatId;
    private String message;
    private long timestamp;
    private boolean viewed;

    public Message() {

    }

    public Message(String messageId, String adSender, String idReceiver, String chatId, String message, long timestamp, boolean viewed) {
        this.messageId = messageId;
        this.idSender = adSender;
        this.idReceiver = idReceiver;
        this.chatId = chatId;
        this.message = message;
        this.timestamp = timestamp;
        this.viewed = viewed;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }
}
