package com.example.carapp.user.ui.home.fragment.shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.R;
import com.example.carapp.Sessions.internetcheck.ConnectionDetector;
import com.example.carapp.db.MainDataBase;
import com.example.carapp.db.ProductDao;
import com.example.carapp.db.ProductEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ShopFragmentViewModel extends ViewModel {
    private Context context;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private List<ProductEntity> productEntitiesList;
    private ProductDao productDao;
    private ProgressDialog progressDialog;
    public MutableLiveData<List<ProductEntity>> productsMutableLiveData;
    public MutableLiveData<String> checkMutableLiveData;
    private DatabaseReference databaseReference;
    private ProductEntity productEntity;
    private String carsListStore, productId, shopName, categoryName, image, name, shopId, price;

    public void init(FragmentActivity activity) {
        this.context = activity;
        productsMutableLiveData = new MutableLiveData<>();
        checkMutableLiveData = new MutableLiveData<>();
        productEntitiesList = new ArrayList<>();
        productDao = MainDataBase.getInstance(context).productDao();
        cd = new ConnectionDetector(context);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.loading));
        progressDialog.setCancelable(true);

    }

    private void getProductsListFromRoom() {

        productDao.getUserProductsData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ProductEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("TAG", "carShop userProductList onSubscribe: ");

                    }

                    @Override
                    public void onNext(List<ProductEntity> productEntityList) {
                        if (productEntityList.size() > 0) {
                            Log.d("TAG", "carShop userProductList onNext:productEntityList>0");

                            productsMutableLiveData.setValue(productEntityList);
                            progressDialog.dismiss();

                        } else {
                            Log.d("TAG", "carShop userProductList onNext: " + String.valueOf(productEntityList.size()));
                            progressDialog.dismiss();
                            checkMutableLiveData.setValue("noInternetConnection");

                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d("TAG", "carShop ShopProductList onError: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "carShop onComplete: ");

                    }
                });

    }

    public void checkInternet() {
        progressDialog.show();
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
        //    getProductsListFromRoom();
            checkMutableLiveData.setValue("noInternetConnection");
            progressDialog.dismiss();
            Log.d("TAG", "carShop  checkInternet:  !isInternetPresent");

        } else {
            Log.d("TAG", "carShop  checkInternet: isInternetPresent");
            checkDataFounded();

        }
    }

    private void checkDataFounded() {

        databaseReference.child("Product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("TAG", "carShop ShopProductList onDataChange:Product child exists");
                    getProducts();

                } else {
                    progressDialog.dismiss();
                    Log.d("TAG", "carShop ShopProductList onDataChange: Product child !exists");
                    checkMutableLiveData.setValue("noProducts");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getProducts() {

        databaseReference.child("Product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    carsListStore = null;

                    productId = snapshot.getKey();
                    name = snapshot.child("name").getValue().toString();
                    image = snapshot.child("image").getValue().toString();
                    shopId = snapshot.child("shopID").getValue().toString();
                    categoryName = snapshot.child("categoryName").getValue().toString();
                    shopName = snapshot.child("shopName").getValue().toString();
                    price = snapshot.child("price").getValue().toString();

                    for (DataSnapshot carSnapshot : snapshot.child("cars").getChildren()) {

                        if (carsListStore == null) {
                            carsListStore = carSnapshot.getValue().toString();

                        } else {
                            carsListStore = carsListStore + "," + carSnapshot.getValue().toString();

                        }

                    }
                    productEntity = new ProductEntity(productId, categoryName, shopId, image, name, carsListStore, shopName, price);
                    productEntitiesList.add(productEntity);


                }

                productsMutableLiveData.setValue(productEntitiesList);

                checkMutableLiveData.setValue("Products");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void insertProductsToRoom(List<ProductEntity> productEntityList){

        Log.d("TAG", "carShop ListToRoom: " + productEntityList.size());
        productDao.deleteAllProductData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "carShop ShopProductList onComplete: delete data to productTable successfully");

                        productDao.insertProductList(productEntitiesList).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CompletableObserver() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d("TAG", "carShop ShopProductList onComplete: add data to productTable successfully");
                                        progressDialog.dismiss();

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d("TAG", "carShop onError: add data to productTable error: " + e);

                                    }
                                });

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", "carShop onError: add data to productTable error: " + e);

                    }
                });







    }


}
