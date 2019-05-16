package com.example.simo.safigeo.Models;


public class Rate {

    String rateId = "";

    //userInfo
    String userId = "";

    String imgId = "";

    int rate =0;

    public Rate()
    {

    }

    public Rate(String rateId, String userId, String imgId, int rate) {
        this.rateId = rateId;
        this.userId = userId;
        this.imgId = imgId;
        this.rate = rate;
    }

    public String getRateId() {
        return rateId;
    }

    public void setRateId(String rateId) {
        this.rateId = rateId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Rate{" +
                "rateId='" + rateId + '\'' +
                ", userId='" + userId + '\'' +
                ", imgId='" + imgId + '\'' +
                ", rate=" + rate +
                '}';
    }
}
