package com.dhkhtn.xk.phuclongserverappver2.Model;

public class Cart {
    private int cId;
    private String cImageCold;
    private String cImageHot;
    private String cName;
    private int cPrice;
    private int cPriceItem;
    private int cPriceTopping;
    private int cQuanity;
    private String cStatus;
    private String cSugar;
    private String cIce;
    private String cTopping;
    private String cComment;

    public Cart(){

    }

    public Cart(int cId, String cImageCold, String cImageHot, String cName, int cPrice, int cPriceItem, int cPriceTopping, int cQuanity, String cStatus, String cSugar, String cIce, String cTopping, String cComment) {
        this.cId = cId;
        this.cImageCold = cImageCold;
        this.cImageHot = cImageHot;
        this.cName = cName;
        this.cPrice = cPrice;
        this.cPriceItem = cPriceItem;
        this.cPriceTopping = cPriceTopping;
        this.cQuanity = cQuanity;
        this.cStatus = cStatus;
        this.cSugar = cSugar;
        this.cIce = cIce;
        this.cTopping = cTopping;
        this.cComment = cComment;
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public String getcImageCold() {
        return cImageCold;
    }

    public void setcImageCold(String cImageCold) {
        this.cImageCold = cImageCold;
    }

    public String getcImageHot() {
        return cImageHot;
    }

    public void setcImageHot(String cImageHot) {
        this.cImageHot = cImageHot;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public int getcPrice() {
        return cPrice;
    }

    public void setcPrice(int cPrice) {
        this.cPrice = cPrice;
    }

    public int getcPriceItem() {
        return cPriceItem;
    }

    public void setcPriceItem(int cPriceItem) {
        this.cPriceItem = cPriceItem;
    }

    public int getcPriceTopping() {
        return cPriceTopping;
    }

    public void setcPriceTopping(int cPriceTopping) {
        this.cPriceTopping = cPriceTopping;
    }

    public int getcQuanity() {
        return cQuanity;
    }

    public void setcQuanity(int cQuanity) {
        this.cQuanity = cQuanity;
    }

    public String getcStatus() {
        return cStatus;
    }

    public void setcStatus(String cStatus) {
        this.cStatus = cStatus;
    }

    public String getcSugar() {
        return cSugar;
    }

    public void setcSugar(String cSugar) {
        this.cSugar = cSugar;
    }

    public String getcIce() {
        return cIce;
    }

    public void setcIce(String cIce) {
        this.cIce = cIce;
    }

    public String getcTopping() {
        return cTopping;
    }

    public void setcTopping(String cTopping) {
        this.cTopping = cTopping;
    }

    public String getcComment() {
        return cComment;
    }

    public void setcComment(String cComment) {
        this.cComment = cComment;
    }
}
