package com.example.carapp.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.carapp.Sessions.sp.TestLogin;
import com.example.carapp.db.MainDataBase;
import com.example.carapp.db.ProductDao;
import com.example.carapp.shop.ui.home.ShopHomeScreen;
import com.example.carapp.start.StartScreen;
import com.example.carapp.user.ui.home.main.UserHomeScreen;


public class SplashLifeCycleObserver implements LifecycleObserver {



    private String TAG = this.getClass().getSimpleName();
    private Context context;

    public SplashLifeCycleObserver(Context context) {
        this.context = context;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStartEvent(){



        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                TestLogin testLogin = new TestLogin(context);

                if (testLogin.getLoginType().equals("shop")) {
                    context.startActivity(new Intent(context, ShopHomeScreen.class));
                    ((Activity)context).finish();
                }
                if (testLogin.getLoginType().equals("user")) {
                    context.startActivity(new Intent(context, UserHomeScreen.class));
                    ((Activity)context).finish();

                } if(testLogin.getLoginType().equals("null")){
                    context.startActivity(new Intent(context, StartScreen.class));
                    ((Activity)context).finish();

                }



            }
        }, 3000);




    }

    }


