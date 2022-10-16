package com.khaolitti.khao;

import java.util.List;

public class FetchModel {

    private String orderId;
    private String status;
    private List<MyOrderItemModel> fetchedOrderList;

    public FetchModel(String orderId, String status, List<MyOrderItemModel> fetchedOrderList) {
        this.orderId = orderId;
        this.status = status;
        this.fetchedOrderList = fetchedOrderList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MyOrderItemModel> getFetchedOrderList() {
        return fetchedOrderList;
    }

    public void setFetchedOrderList(List<MyOrderItemModel> fetchedOrderList) {
        this.fetchedOrderList = fetchedOrderList;
    }
}
