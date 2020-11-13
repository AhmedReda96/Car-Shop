package com.example.carapp.user.ui.home.fragment.cart;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.db.CartEntity;
import com.example.carapp.db.MainDataBase;
import com.example.carapp.db.ProductDao;
import com.example.carapp.db.ProductEntity;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CartFragmentViewModel extends ViewModel {
    private Context context;
    private ProductDao productDao;
    public MutableLiveData<List<CartEntity>> cartLiveData;


    public void init(Activity activity) {
        this.context = activity;
        cartLiveData = new MutableLiveData<>();
        productDao = MainDataBase.getInstance(context).productDao();


    }

    public void getProductData() {
        productDao.getCartData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CartEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("TAG", "carShop cart onSubscribe: ");

                    }

                    @Override
                    public void onNext(List<CartEntity> productEntityList) {
                        Log.d("TAG", "carShop cart onNext: " + productEntityList.size());

                        cartLiveData.setValue(productEntityList);


                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d("TAG", "carShop cart onError: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.d("Tag", "carShop cart : onComplete get research data ");

                    }
                });


    }

    public void checkout() {
        productDao.deleteCartData().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "carShop StartScreen onComplete: delete cart table from room successfully");
                        Toast.makeText(context, "Checkout Successfully", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", "carShop StartScreen onError: delete cart table from room error:" + e.getMessage());

                    }
                });


    }
}
