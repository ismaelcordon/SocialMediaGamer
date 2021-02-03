package com.icdominguez.socialmediagamer.models;

public class Like {

    private String likeId;
    private String postId;
    private String userId;
    private long timestamp;

    public Like() {

    }

    public Like(String likeId, String postId, String userId, long timestamp) {
        this.likeId = likeId;
        this.postId = postId;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
