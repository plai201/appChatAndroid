package com.example.appchatandroidt.models;

public class Friends {
    private String name;
    private String sdt;
    private String avatarUrl;
    public Friends(){

    }

    public Friends(String name, String sdt, String avatarUrl) {
        this.name = name;
        this.sdt = sdt;
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
