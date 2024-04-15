package com.example.appchatandroidt.models;

public class User {
    private String id;
    private String avatarUrl;
    private String name;
    private String email;
    private String sdt;
    private String passWord;
    private int role;



    public User() {
     }


    public User(String id,String avatarUrl, String name, String email, String sdt,int role) {
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.email = email;
        this.sdt = sdt;
        this.role =role;
     }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
