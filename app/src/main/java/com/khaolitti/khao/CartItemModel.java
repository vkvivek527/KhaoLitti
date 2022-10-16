package com.khaolitti.khao;

import java.util.ArrayList;
import java.util.List;

public class CartItemModel

{
    public static final int CART_ITEM=0;
    public static final int TOTAL_AMOUNT=1;

    private int Type;

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }
    //////cart item
    private String productID;
    private String productImage;
    private String productTitle;
    private Long freeCoupans;
    private String productPrice;
    private String cuttedPrice;
    private Long productQuantity;
    private Long maxQuantity;
    private Long stockQuantity;
    private Long offersApplied;
    private Long coupanApplied;
    private boolean inStock;
    private List<String> qtyId;
    private boolean qtyError;
    private String selectedCoupanId;
    private String discountedPrice;
    private boolean COD;


    public CartItemModel(boolean COD,int type, String productID, String productImage, String productTitle, Long freeCoupans, String productPrice, String cuttedPrice, Long productQuantity, Long offersApplied, Long coupanApplied, boolean inStock, Long maxQuantity, Long stockQuantity) {
        Type = type;
        this.productID=productID;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.freeCoupans = freeCoupans;
        this.productPrice = productPrice;
        this.cuttedPrice = cuttedPrice;
        this.productQuantity = productQuantity;
        this.offersApplied = offersApplied;
        this.coupanApplied = coupanApplied;
        this.maxQuantity=maxQuantity;
        this.inStock=inStock;
        this.stockQuantity=stockQuantity;
        qtyId=new ArrayList<>();
        qtyError=false;
        this.COD=COD;

    }

    public boolean isCOD() {
        return COD;
    }

    public void setCOD(boolean COD) {
        this.COD = COD;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getSelectedCoupanId() {
        return selectedCoupanId;
    }

    public void setSelectedCoupanId(String selectedCoupanId) {
        this.selectedCoupanId = selectedCoupanId;
    }

    public List<String> getQtyId() {
        return qtyId;
    }

    public Long getStockQuantity() {
        return stockQuantity;
    }

    public boolean isQtyError() {
        return qtyError;
    }

    public void setQtyError(boolean qtyError) {
        this.qtyError = qtyError;
    }

    public void setStockQuantity(Long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setQtyId(List<String> qtyId) {
        this.qtyId = qtyId;
    }

    public String getProductID() {
        return productID;
    }

    public boolean isInStock() {
        return inStock;
    }

    public Long getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Long maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public void setProductID(String productID) {
        this.productID = productID;
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

    public Long getFreeCoupans() {
        return freeCoupans;
    }

    public void setFreeCoupans(Long freeCoupans) {
        this.freeCoupans = freeCoupans;
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

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Long getOffersApplied() {
        return offersApplied;
    }

    public void setOffersApplied(Long offersApplied) {
        this.offersApplied = offersApplied;
    }

    public Long getCoupanApplied() {
        return coupanApplied;
    }

    public void setCoupanApplied(Long coupanApplied) {
        this.coupanApplied = coupanApplied;
    }

    /////cart item


    //////cart total

    int totalItems;
    int totalItemsPrice;
    String deliveryPrice;
    int totalAmount;
    int savedAmoun;

    public CartItemModel(int type) {
        Type = type;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalItemsPrice() {
        return totalItemsPrice;
    }

    public void setTotalItemsPrice(int totalItemsPrice) {
        this.totalItemsPrice = totalItemsPrice;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getSavedAmoun() {
        return savedAmoun;
    }

    public void setSavedAmoun(int savedAmoun) {
        this.savedAmoun = savedAmoun;
    }
    /////cart total

}
