package com.example.carapp.user.ui.productDetails;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.R;
import com.example.carapp.db.CartEntity;
import com.example.carapp.db.MainDataBase;
import com.example.carapp.db.ProductDao;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProductDetailsViewModel extends ViewModel {
    private ProgressDialog progressDialog;
    String productID, name, shopName, categoryName, cars, price, count, image;
    private Context context;
    private ProductDao productDao;
    private CartEntity cartEntity;
    public MutableLiveData<String> checkMutableLiveData;

    public void init(Activity activity) {
        this.context=activity;
        productDao = MainDataBase.getInstance(context).productDao();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        checkMutableLiveData = new MutableLiveData<>();

    }


    public void addProductToCart(String productID, String name, String shopName, String categoryName, String cars, String price, String count, String image) {
        this.cars = cars;
        this.productID = productID;
        this.name = name;
        this.shopName = shopName;
        this.categoryName = categoryName;
        this.price = price;
        this.count = count;
        this.image = image;

        addCartItemToRoom();

    }

    private void addCartItemToRoom() {
        progressDialog.show();

        cartEntity = new CartEntity(productID, categoryName, image, name, price, cars, shopName, count);
        productDao.insertProductToCart(cartEntity).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "carShop addItemToCart onComplete: add product to room successfully");
                        ((Activity) context).finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", "carShop addItemToCart onError: add product to room error:"+e);
                        progressDialog.dismiss();
                        checkMutableLiveData.setValue("error");

                    }
                });

    }
}
