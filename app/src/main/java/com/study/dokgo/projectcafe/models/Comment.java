package com.study.dokgo.projectcafe.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dokgo on 30.12.16.
 */

public class Comment {

    @SerializedName("commentId")
    private String commentId;
    @SerializedName("cafeId")
    private String cafeId;
    @SerializedName("email")
    private String email;
    @SerializedName("content")
    private String content;
    @SerializedName("rate")
    private String rate;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCafeId() {
        return cafeId;
    }

    public void setCafeId(String cafeId) {
        this.cafeId = cafeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return getEmail();
    }

}