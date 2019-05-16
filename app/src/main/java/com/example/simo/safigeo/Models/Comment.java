package com.example.simo.safigeo.Models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {

    String commentId = "";

    //user info
    String userId = "";
    String userName = "";
    String userProfileImgUrl= "";

    //CommentInfo
    String commentString = "";
    String commentDate = "";

    //ImageInfo
    String imgId = "";

    public Comment() {
    }

    public Comment(String commentId, String userId, String userName, String userProfileImgUrl, String commentString, String imgId) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.userProfileImgUrl = userProfileImgUrl;
        this.commentString = commentString;
        this.commentDate = new SimpleDateFormat("MMMM dd',' yyyy 'at' HH:mm").format(new Date());;
        this.imgId = imgId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfileImgUrl() {
        return userProfileImgUrl;
    }

    public void setUserProfileImgUrl(String userProfileImgUrl) {
        this.userProfileImgUrl = userProfileImgUrl;
    }

    public String getCommentString() {
        return commentString;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId='" + commentId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userProfileImgUrl='" + userProfileImgUrl + '\'' +
                ", commentString='" + commentString + '\'' +
                ", commentDate='" + commentDate + '\'' +
                ", imgId='" + imgId + '\'' +
                '}';
    }
}
