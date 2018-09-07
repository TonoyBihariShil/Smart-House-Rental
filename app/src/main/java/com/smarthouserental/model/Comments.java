package com.smarthouserental.model;

public class Comments {
    private String comment;
    private String name;
    private String commentId;

    public Comments() {
    }

    public Comments(String comment, String name) {
        this.comment = comment;
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public String getName() {
        return name;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
