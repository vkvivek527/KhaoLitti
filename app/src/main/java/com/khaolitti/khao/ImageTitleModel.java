package com.khaolitti.khao;

public class ImageTitleModel {

    private String title;
    private String image;
    private int quantity;
    private String price;
    private String cuutedprice;

    public ImageTitleModel(String title, String image, int quantity, String price, String cuutedprice) {
        this.title = title;
        this.image = image;
        this.quantity = quantity;
        this.price = price;
        this.cuutedprice = cuutedprice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCuutedprice() {
        return cuutedprice;
    }

    public void setCuutedprice(String cuutedprice) {
        this.cuutedprice = cuutedprice;
    }
}
