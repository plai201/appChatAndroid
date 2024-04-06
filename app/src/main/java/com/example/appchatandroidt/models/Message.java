package com.example.appchatandroidt.models;

import com.google.firebase.auth.FirebaseAuth;

public class Message {

    private String sms,status,userId,datetime;
    private boolean isImageMessage;

    public boolean isImageMessage() {
        return isImageMessage;
    }

    public void setImageMessage(boolean imageMessage) {
        isImageMessage = imageMessage;
    }

    public Message(){

    }

    public Message(String sms, String status, String userId, String datetime,boolean isImageMessage) {
        this.sms = sms;
        this.status = status;
        this.userId = userId;
        this.datetime = datetime;
        this.isImageMessage = isImageMessage;
    }
    public boolean isSentByCurrentUser() {
        // trả về true nếu là người gửi
        return userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
