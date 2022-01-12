package com.example.saysomethings.Model;

public class User {

    private String username;
    private String bio;
    private String email;
    private String id;
    private String imageurl;
    private String phone;
    private String status;

    public User(String username, String bio, String email, String id, String imageurl, String phone,String status) {
        this.username = username;
        this.bio = bio;
        this.email = email;
        this.id = id;
        this.imageurl = imageurl;
        this.phone = phone;
        this.status = status;
    }
    public User()
    {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
