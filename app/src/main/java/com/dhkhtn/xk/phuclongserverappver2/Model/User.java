package com.dhkhtn.xk.phuclongserverappver2.Model;

public class User {
    String phone;
    String name;
    String address;
    int active;
    int noti_history;
    int noti_news;
    String error_msg;

    public User() {

    }

    public User(String phone, String name, String address, int active, int noti_history, int noti_news, String error_msg) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.active = active;
        this.noti_history = noti_history;
        this.noti_news = noti_news;
        this.error_msg = error_msg;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getNoti_history() {
        return noti_history;
    }

    public void setNoti_history(int noti_history) {
        this.noti_history = noti_history;
    }

    public int getNoti_news() {
        return noti_news;
    }

    public void setNoti_news(int noti_news) {
        this.noti_news = noti_news;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
