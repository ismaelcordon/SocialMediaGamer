package com.icdominguez.socialmediagamer.models;

import java.util.ArrayList;

public class Chat {
    private String chatId;
    private String userId1;
    private String userId2;
    private Boolean isWriting;
    private long timestamp;
    private ArrayList<String> usersIds;
    private int notificationId;

    public Chat() {

    }

    public Chat(String chatId, String userId1, String userId2, Boolean isWriting, long timestamp, ArrayList<String> usersIds, int notificationId) {
        this.chatId = chatId;
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.isWriting = isWriting;
        this.timestamp = timestamp;
        this.usersIds = usersIds;
        this.notificationId = notificationId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public void setUserId2(String userId2) {
        this.userId2 = userId2;
    }

    public Boolean getWriting() {
        return isWriting;
    }

    public void setWriting(Boolean writing) {
        isWriting = writing;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(ArrayList<String> usersIds) {
        this.usersIds = usersIds;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}
