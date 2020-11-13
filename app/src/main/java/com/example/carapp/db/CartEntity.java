package com.example.carapp.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cartTable")
public class CartEntity {
    @PrimaryKey
    @NonNull
    private String productId;
    private String categoryName,image, name,price,cars,shopName,count;


    public CartEntity(@NonNull String productId, String categoryName, String image, String name, String price, String cars, String shopName, String count) {
        this.productId = productId;
        this.categoryName = categoryName;
        this.image = image;
        this.name = name;
        this.price = price;
        this.cars = cars;
        this.shopName = shopName;
        this.count = count;
    }

    @NonNull
    public String getProductId() {
        return productId;
    }

    public void setProductId(@NonNull String productId) {
        this.productId = productId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCars() {
        return cars;
    }

    public void setCars(String cars) {
        this.cars = cars;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
