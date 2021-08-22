package com.newsapp.foodorderapp.food_cart;

import com.newsapp.foodorderapp.foods_list.FoodsModel;

import java.util.List;
import java.util.Map;

public class OrderPlacedModel {

    String name;
    String phone;
    String address;
    String total;
   // Map<String, Object>  ordersList;
   List<String> ordersList;
    public OrderPlacedModel() {
    }

    public OrderPlacedModel(String name, String phone, String address, String total, List<String>  ordersList) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.total = total;
        this.ordersList = ordersList;
    }

    public List<String>  getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<String>  ordersList) {
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
