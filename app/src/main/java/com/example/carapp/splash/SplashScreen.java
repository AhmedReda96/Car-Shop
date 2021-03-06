package com.example.carapp.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;


import com.example.carapp.R;

public class SplashScreen extends AppCompatActivity {
    private int backFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init();


    }

    private void init() {
        getLifecycle().addObserver(new SplashLifeCycleObserver(SplashScreen.this));
    }

    @Override
    public void onBackPressed() {

        backFlag++;

        if (backFlag == 2) {
            finish();
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                backFlag = 0;
            }
        }, 2000);
    }

}
