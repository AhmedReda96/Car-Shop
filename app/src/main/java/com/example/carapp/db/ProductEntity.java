package com.example.carapp.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ProductTable")
public class ProductEntity {
    @PrimaryKey
    @NonNull
    private String productId;
    private String categoryName, shopID;
    private String image, name;
    private String cars,shopName;
    private String price;


    public ProductEntity(@NonNull String productId, String categoryName, String shopID, String image, String name, String cars, String shopName, String price) {
        this.productId = productId;
        this.categoryName = categoryName;
        this.shopID = shopID;
        this.image = image;
        this.name = name;
        this.cars = cars;
        this.shopName = shopName;
        this.price = price;
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

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
