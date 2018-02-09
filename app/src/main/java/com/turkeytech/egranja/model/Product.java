package com.turkeytech.egranja.model;

public class Product {


    private String userid;
    private String username;
    private String usernum;
    private String timeStamp;
    private String productId;
    private String name;
    private String description;
    private String quantity;
    private double price;
    private String category;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
    private String location;
    private String audio;
    private String video;

    public Product() {
    }

    public Product(String userid, String username, String usernum, String name, String description,
                   String quantity, double price, String category, String image1, String image2,
                   String image3, String image4, String location) {

        this.userid = userid;
        this.username = username;
        this.usernum = usernum;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.location = location;

    }

    public String getUserid() {
        return userid;
    }

    public String getUsername() {
        return username;
    }

    public String getUsernum() {
        return usernum;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getImage1() {
        return image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getImage3() {
        return image3;
    }

    public String getImage4() {
        return image4;
    }

    public String getLocation() {
        return location;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}