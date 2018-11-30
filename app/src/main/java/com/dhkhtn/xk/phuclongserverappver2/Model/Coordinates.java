package com.dhkhtn.xk.phuclongserverappver2.Model;

public class Coordinates {
    double lat;
    double lng;
    String address;

    public Coordinates(){

    }

    public Coordinates(double lat, double lng, String address) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
