package com.example.carapp.Sessions.sp;

import android.content.Context;
import android.content.SharedPreferences;

public class TestLogin {

    private Context context;
    private String auth = "null";


    private SharedPreferences sharedPreferences;


    public TestLogin(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("TestLogin", Context.MODE_PRIVATE);
    }

    public void remove() {
        setLoginType("null");
    }


    public void setLoginType(String auth) {
        this.auth = auth;
        sharedPreferences.edit().putString("Auth", auth).commit();
    }

    public String getLoginType() {
        auth = sharedPreferences.getString("Auth", auth);
        return auth;
    }


}


