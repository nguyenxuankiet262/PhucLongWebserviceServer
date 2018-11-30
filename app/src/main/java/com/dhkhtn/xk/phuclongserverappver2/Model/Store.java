package com.dhkhtn.xk.phuclongserverappver2.Model;

public class Store {
    String id;
    String address;
    String name;
    String image;
    String lat;
    String lng;

    public Store(String id, String address, String name, String image, String lat, String lng) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.image = image;
        this.lat = lat;
        this.lng = lng;
    }

    public Store(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
