package com.smarthouserental.model;

public class RentalPost {
    private String userName;
    private String profileImage;
    private String postImage;
    private String distance;
    private String time;
    private String area;
    private String type;
    private String details;
    private String price;
    private String address;
    private String postId;
    private double s_lattude;
    private double s_longitide;
    private double d_lattude;
    private double d_longitide;


    public RentalPost(String userName, String profileImage, String postImage, String distance, String time, String area, String type, String details, String price, String address, String postId, double s_lattude, double s_longitide, double d_lattude, double d_longitide) {
        this.userName = userName;
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.distance = distance;
        this.time = time;
        this.area = area;
        this.type = type;
        this.details = details;
        this.price = price;
        this.address = address;
        this.postId = postId;
        this.s_lattude = s_lattude;
        this.s_longitide = s_longitide;
        this.d_lattude = d_lattude;
        this.d_longitide = d_longitide;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public String getDistance() {
        return distance;
    }

    public String getTime() {
        return time;
    }

    public String getArea() {
        return area;
    }

    public String getType() {
        return type;
    }

    public String getDetails() {
        return details;
    }

    public String getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    public String getPostId() {
        return postId;
    }

    public double getS_lattude() {
        return s_lattude;
    }

    public double getS_longitide() {
        return s_longitide;
    }

    public double getD_lattude() {
        return d_lattude;
    }

    public double getD_longitide() {
        return d_longitide;
    }
}
