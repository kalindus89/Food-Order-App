package com.newsapp.foodorderapp.all_foods_home;

public class BannerModel {

    String foodId;
    String image;
    String name;



    public BannerModel() {
    }

    public BannerModel(String foodId, String image, String name) {
        this.foodId = foodId;
        this.image = image;
        this.name = name;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
