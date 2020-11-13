package com.example.carapp.Sessions.sp;

import android.content.Context;
import android.content.SharedPreferences;

public class ShopData {


    private Context context;
    private String image = "null";
    private String id = "null";
    private String name = "null";


    private SharedPreferences sharedPreferences;


    public ShopData(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("ShopData", Context.MODE_PRIVATE);
    }

    public void remove() {
        setImage("null");
        setId("null");
        setName("null");

    }


    public void setImage(String image) {
        this.image = image;
        sharedPreferences.edit().putString("image", image).commit();
    }

    public String getImage() {
        image = sharedPreferences.getString("image", image);
        return image;
    }


    public void setId(String id) {
        this.id = id;
        sharedPreferences.edit().putString("id", id).commit();
    }

    public String getId() {
        id = sharedPreferences.getString("id", id);
        return id;
    }


    public void setName(String name) {
        this.name = name;
        sharedPreferences.edit().putString("name", name).commit();
    }

    public String getName() {
        name = sharedPreferences.getString("name", name);
        return name;
    }
}
