package com.icdominguez.socialmediagamer.models;

public class Post {

    private String postId;
    private String title;
    private String description;
    private String image1;
    private String image2;
    private String userId;
    private String category;
    private long timestamp;

    public Post() {

    }

    public Post(String postId, String title, String description, String image1, String image2, String userId, String category, long timestamp) {
        this.postId = postId;
        this.title = title;
        this.description = description;
        this.image1 = image1;
        this.image2 = image2;
        this.userId = userId;
        this.category = category;
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }



    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
