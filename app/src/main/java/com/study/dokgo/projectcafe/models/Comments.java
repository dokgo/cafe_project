package com.study.dokgo.projectcafe.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dokgo on 30.12.16.
 */

public class Comments {

    @SerializedName("comments")
    private List<Comment> comments = null;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "";
    }

}