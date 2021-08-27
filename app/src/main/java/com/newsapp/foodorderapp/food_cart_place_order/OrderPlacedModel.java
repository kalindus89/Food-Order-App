package com.newsapp.foodorderapp.food_cart_place_order;

import java.util.List;

public class OrderPlacedModel {

    String name;
    String phone;
    String address;
    String total;
    List<CartModel> ordersList;
   String status;
    public OrderPlacedModel() {
    }

    public OrderPlacedModel(String name, String phone, String address, String total, List<CartModel>  ordersList) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.total = total;
        this.ordersList = ordersList;
        status="0";  // 0=placed, 1=shipping, 2=shipped
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CartModel>  getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<CartModel>  ordersList) {
        this.ordersList = ordersList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
