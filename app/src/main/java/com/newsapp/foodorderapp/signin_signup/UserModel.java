package com.newsapp.foodorderapp.signin_signup;

public class UserModel {
    String name;
    String password;
    String subscribeState;

    public UserModel() {

    }

    public UserModel(String name, String password, String subscribeState) {
        this.name = name;
        this.password = password;
        this.subscribeState = subscribeState;
    }

    public String getSubscribeState() {
        return subscribeState;
    }

    public void setSubscribeState(String subscribeState) {
        this.subscribeState = subscribeState;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
