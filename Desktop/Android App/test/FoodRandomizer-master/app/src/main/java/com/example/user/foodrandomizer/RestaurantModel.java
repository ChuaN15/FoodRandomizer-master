package com.example.user.foodrandomizer;



public class RestaurantModel {

    private String item_name,item_place,item_price;

    public RestaurantModel(String item_name, String item_place, String item_price) {
        this.item_name = item_name;
        this.item_place = item_place;
        this.item_price = item_price;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_place() {
        return item_place;
    }

    public void setItem_place(String item_place) {
        this.item_place = item_place;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }

}
