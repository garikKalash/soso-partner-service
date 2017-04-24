package com.soso.models;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */
public class Partner {
    private Integer id;
    private String name;
    private String telephone;
    private boolean isAdmin;
    private Integer serviceId;
    private String username;
    private String password;
    private String notices;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String address;
    private Integer imgId;
    private String imgpath;
    private List<String> images;
    private List<Feedback> feedbacks;
    private List<String> followers;

    private boolean reservable;

    public Partner(){

    }

    public Partner(Integer id, String telephone, String address) {
        this.id = id;
        this.telephone = telephone;
        this.address = address;
    }

    public Partner(Integer id, String name, String telephone,
                   boolean isAdmin, Integer serviceId, String username,
                   String password, String notices, BigDecimal longitude,
                   BigDecimal latitude, String address,Integer imgId, String imgpath,
                   List<String> images, List<Feedback> feedbacks, List<String> followers,boolean reservable) {
        this.id = id;
        this.name = name;
        this.telephone = telephone;
        this.isAdmin = isAdmin;
        this.serviceId = serviceId;
        this.username = username;
        this.password = password;
        this.notices = notices;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.imgId = imgId;
        this.imgpath = imgpath;
        this.images = images;
        this.feedbacks = feedbacks;
        this.followers = followers;
        this.reservable = reservable;
    }

    public boolean isReservable() {
        return reservable;
    }

    public void setReservable(boolean reservable) {
        this.reservable = reservable;
    }

    public Integer getId() {
        return id;
    }

    public Integer getImgId() {
        return imgId;
    }

    public void setImgId(Integer imgId) {
        this.imgId = imgId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getName() {
        return name;
    }

    public void setName(String partner_name) {
        this.name = partner_name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String partner_username) {
        this.username = partner_username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNotices() {
        return notices;
    }

    public void setNotices(String notices) {
        this.notices = notices;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
}
