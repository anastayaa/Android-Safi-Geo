package com.example.simo.safigeo.Models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Place {

    //user info
    String userId = "";
    String userName = "";
    String userEmail = "";
    String userProfileImgUrl= "";

    //Place info
    String placeId= "";
    String placeImgUrl= "";
    String placeName= "";
    String placeDescription= "";
    String placeTags= "";
    String placeUploadDate= "";

    Double longitude = 0.0;
    Double latitude = 0.0;

    public Place() {

        placeUploadDate = new SimpleDateFormat("MMMM dd',' yyyy 'at' HH:mm").format(new Date());

    }

    public Place(FirebaseUser user, String id, String url, String name , String Desc , String tags , Double lng, Double lat)
    {
        userId = user.getUid();
        userName = user.getDisplayName();
        userEmail = user.getEmail();
        userProfileImgUrl = user.getPhotoUrl().toString();

        placeId = id;
        placeImgUrl = url;
        placeName = name;
        placeDescription = Desc;
        placeTags = tags;


        longitude = lng;
        latitude = lat;

    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getPlaceUploadDate() {
        return placeUploadDate;
    }

    public void setPlaceUploadDate(String placeUploadDate) {
        this.placeUploadDate = placeUploadDate;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserProfileImgUrl() {
        return userProfileImgUrl;
    }

    public void setUserProfileImgUrl(String userProfileImgUrl) {
        this.userProfileImgUrl = userProfileImgUrl;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceImgUrl() {
        return placeImgUrl;
    }

    public void setPlaceImgUrl(String placeImgUrl) {
        this.placeImgUrl = placeImgUrl;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceDescription() {
        return placeDescription;
    }

    public void setPlaceDescription(String placeDescription) {
        this.placeDescription = placeDescription;
    }

    public String getPlaceTags() {
        return placeTags;
    }

    public void setPlaceTags(String placeTags) {
        this.placeTags = placeTags;
    }





    public void setUser(FirebaseUser user) {
        userId = user.getUid();
        userName = user.getDisplayName();
        userEmail = user.getEmail();
        userProfileImgUrl = user.getPhotoUrl().toString();
    }

    @Override
    public String toString() {
        return "Place{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userProfileImgUrl='" + userProfileImgUrl + '\'' +
                ", placeId='" + placeId + '\'' +
                ", placeImgUrl='" + placeImgUrl + '\'' +
                ", placeName='" + placeName + '\'' +
                ", placeDescription='" + placeDescription + '\'' +
                ", placeTags='" + placeTags + '\'' +
                ", placeUploadDate='" + placeUploadDate + '\'' +
                ", Longitude=" + longitude +
                ", Latitude=" + latitude +
                '}';
    }
}
