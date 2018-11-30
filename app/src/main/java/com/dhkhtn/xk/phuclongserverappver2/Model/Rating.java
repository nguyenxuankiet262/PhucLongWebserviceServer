package com.dhkhtn.xk.phuclongserverappver2.Model;

public class Rating {
    private int ID;
    private String userID;
    private int drinkID;
    private int rate;
    private String comment;
    private String date;
    String error_msg;

    public Rating(){

    }

    public Rating(int ID, String userID, int drinkID, int rate, String comment, String date, String error_msg) {
        this.ID = ID;
        this.userID = userID;
        this.drinkID = drinkID;
        this.rate = rate;
        this.comment = comment;
        this.date = date;
        this.error_msg = error_msg;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getDrinkID() {
        return drinkID;
    }

    public void setDrinkID(int drinkID) {
        this.drinkID = drinkID;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}