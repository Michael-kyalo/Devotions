package com.oyeafrica.devotions.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Devotion {
    private String text;
    private String asset;
    private String type;
    private String creater_id;
    private String poster_id;
    private String devotion_id;
    @ServerTimestamp
    Date date;

    public Devotion() {
    }

    public Devotion(String text, String asset, String type, String creater_id, String poster_id, String devotion_id, Date date) {
        this.text = text;
        this.asset = asset;
        this.type = type;
        this.creater_id = creater_id;
        this.poster_id = poster_id;
        this.devotion_id = devotion_id;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDevotioun_id() {
        return devotion_id;
    }

    public void setDevotion_id(String devotion_id) {
        this.devotion_id = devotion_id;
    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreater_id() {
        return creater_id;
    }

    public void setCreater_id(String creater_id) {
        this.creater_id = creater_id;
    }

    public String getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(String poster_id) {
        this.poster_id = poster_id;
    }
}
