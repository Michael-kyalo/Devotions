package com.oyeafrica.devotions.models;

public class User {
    String uid;
    String username;
    String type;
    String image;

    public User() {
    }

    public User(String uid, String username, String type, String image) {
        this.uid = uid;
        this.username = username;
        this.type = type;
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
