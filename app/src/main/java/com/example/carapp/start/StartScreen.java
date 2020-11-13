package com.example.carapp.start;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.carapp.R;
import com.example.carapp.Sessions.sp.TestLogin;
import com.example.carapp.databinding.ActivityStartScreenBinding;
import com.example.carapp.db.MainDataBase;
import com.example.carapp.db.ProductDao;
import com.example.carapp.shop.ui.login.signIn.ShopSignInScreen;
import com.example.carapp.user.ui.login.signin.UserSignInScreen;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StartScreen extends AppCompatActivity implements View.OnClickListener {
    private ActivityStartScreenBinding binding;
    private TestLogin testLogin;
    private int backFlag = 0;
    private ProductDao productDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        init();
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start_screen);
        binding.setLifecycleOwner(this);

        testLogin = new TestLogin(this);
        testLogin.remove();

        binding.shopBtn.setOnClickListener(this::onClick);
        binding.userBtn.setOnClickListener(this::onClick);




        deleteRoomDatabase();
    }

    private void deleteRoomDatabase() {

        productDao = MainDataBase.getInstance(this).productDao();
        productDao.deleteAllProductData().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "carShop StartScreen onComplete: delete product table from room successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", "carShop StartScreen onError: delete product table from room error");

                    }
                });


        productDao.deleteCartData().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "carShop StartScreen onComplete: delete cart table from room successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", "carShop StartScreen onError: delete cart table from room error");

                    }
                });

    }

    @Override
    public void onClick(View v) {
        if (binding.shopBtn.equals(v)) {
            startActivity(new Intent(StartScreen.this, ShopSignInScreen.class));


        }
        if (binding.userBtn.equals(v)) {
            startActivity(new Intent(StartScreen.this, UserSignInScreen.class));
        }
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
