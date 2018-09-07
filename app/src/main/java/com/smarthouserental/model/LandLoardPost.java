package com.smarthouserental.model;

public class LandLoardPost {
    private String area;
    private String details;
    private String latitude;
    private String longitude;
    private String postId;
    private String type;
    private String userName;
    private String userId;
    private String image;
    private String price;
    private String address;

    public LandLoardPost() {
    }

    public LandLoardPost(String area, String details, String latitude, String longitude, String postId, String type, String userName, String userId, String image, String price, String address) {
        this.area = area;
        this.details = details;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postId = postId;
        this.type = type;
        this.userName = userName;
        this.userId = userId;
        this.image = image;
        this.price = price;
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public String getDetails() {
        return details;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPostId() {
        return postId;
    }

    public String getType() {
        return type;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }
}
