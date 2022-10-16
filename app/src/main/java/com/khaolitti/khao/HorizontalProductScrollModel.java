package com.khaolitti.khao;

public class HorizontalProductScrollModel {
    private String productId;
    private String productImage;
    private String productTitle;
    private String productDiscription;
    private String productPrice;
    private String cuttedPrice;

    public HorizontalProductScrollModel(String productId,String productImage, String productTitle, String productDiscription, String productPrice,String cuttedPrice) {
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productDiscription = productDiscription;
        this.productPrice = productPrice;
        this.productId=productId;
        this.cuttedPrice=cuttedPrice;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
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

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDiscription() {
        return productDiscription;
    }

    public void setProductDiscription(String productDiscription) {
        this.productDiscription = productDiscription;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
