package com.dhkhtn.xk.phuclongserverappver2.Model;

import java.io.Serializable;

public class Drink implements Serializable {
    int ID;
    String imageCold;
    String imageHot;
    int categoryID;
    String Name;
    int Price;

    public Drink(){

    }

    public Drink(int ID, String imageCold, String imageHot, int categoryID, String name, int price) {
        this.ID = ID;
        this.imageCold = imageCold;
        this.imageHot = imageHot;
        this.categoryID = categoryID;
        Name = name;
        Price = price;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getImageCold() {
        return imageCold;
    }

    public void setImageCold(String imageCold) {
        this.imageCold = imageCold;
    }

    public String getImageHot() {
        return imageHot;
    }

    public void setImageHot(String imageHot) {
        this.imageHot = imageHot;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }
}
