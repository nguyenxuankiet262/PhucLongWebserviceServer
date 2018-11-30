package com.dhkhtn.xk.phuclongserverappver2.Model;

import java.io.Serializable;

public class Order implements Serializable {
    private int id;
    private String address;
    private String name;
    private String note;
    private String payment;
    private String phone;
    private String price;
    private String timeorder;
    private String drinkdetail;
    private int status;
    private int storeID;


    public Order(){

    }

    public Order(int id, String address, String name, String note, String payment, String phone, String price, String timeorder, String drinkdetail, int status, int storeID) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.note = note;
        this.payment = payment;
        this.phone = phone;
        this.price = price;
        this.timeorder = timeorder;
        this.drinkdetail = drinkdetail;
        this.status = status;
        this.storeID = storeID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTimeorder() {
        return timeorder;
    }

    public void setTimeorder(String timeorder) {
        this.timeorder = timeorder;
    }

    public String getDrinkdetail() {
        return drinkdetail;
    }

    public void setDrinkdetail(String drinkdetail) {
        this.drinkdetail = drinkdetail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }
}
