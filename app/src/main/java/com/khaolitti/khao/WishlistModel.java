package com.khaolitti.khao;

import java.util.ArrayList;

public class WishlistModel {

    private String productId;
    private String productImage;
    private String title;
    private long freecoupan;
    private String rating;
    private long totalrating;
    private String productPrice;
    private String cuttedPrice;
    private Boolean COD;
    private boolean inStock;
    private ArrayList<String> tags;

    public WishlistModel(String productId,String productImage, String title, long freecoupan, String rating, long totalrating, String productPrice, String cuttedPrice, Boolean COD,boolean inStock) {
        this.productId=productId;
        this.productImage = productImage;
        this.title = title;
        this.freecoupan = freecoupan;
        this.rating = rating;
        this.totalrating = totalrating;
        this.productPrice = productPrice;
        this.cuttedPrice = cuttedPrice;
        this.COD = COD;
        this.inStock=inStock;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImage() {
        return productImage;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getFreecoupan() {
        return freecoupan;
    }

    public void setFreecoupan(long freecoupan) {
        this.freecoupan = freecoupan;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public long getTotalrating() {
        return totalrating;
    }

    public void setTotalrating(long totalrating) {
        this.totalrating = totalrating;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }

    public Boolean getCOD() {
        return COD;
    }

    public void setCOD(Boolean COD) {
        this.COD = COD;
    }
}
