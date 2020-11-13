package com.example.carapp.shop.ui.products.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.R;
import com.example.carapp.Sessions.internetcheck.ConnectionDetector;
import com.example.carapp.Sessions.sp.ShopData;
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

public class ProductsListViewModel extends ViewModel {
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private List<ProductEntity> productEntitiesList = new ArrayList<>();
    private ProductDao productDao;
    private Context context;
    private ShopData shopData;
    private ProgressDialog progressDialog;
    public MutableLiveData<List<ProductEntity>> productsMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<String> checkMutableLiveData = new MutableLiveData<>();
    private DatabaseReference databaseReference;
    private ProductEntity productEntity;
    private String categoryName;
    private String carsListStore ;

    private List<ProductEntity> productEntityList = new ArrayList<>();

    public void getProductsListFromRoom(String catName, ProductsListScreen activity) {
        this.categoryName = catName;
        this.context = activity;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.loading));
        progressDialog.setCancelable(true);
        progressDialog.show();
        productDao = MainDataBase.getInstance(context).productDao();


        productDao.getProductData(categoryName).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ProductEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("TAG", "carShop ShopProductList onSubscribe: ");

                    }

                    @Override
                    public void onNext(List<ProductEntity> productEntityList) {
                        if (productEntityList.size() > 0) {
                            Log.d("TAG", "carShop ShopProductList onNext:productEntityList>0");

                            productsMutableLiveData.setValue(productEntityList);
                            progressDialog.dismiss();

                        } else {
                            Log.d("TAG", "carShop ShopProductList onNext: " + String.valueOf(productEntityList.size()));
                            checkInternet();

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

    private void checkInternet() {
        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            Log.d("TAG", "carShop ShopProductList checkInternet:  !isInternetPresent");
            progressDialog.dismiss();
            checkMutableLiveData.setValue("noInternetConnection");
        } else {
            Log.d("TAG", "carShop ShopProductList checkInternet: isInternetPresent");
            checkDataFounded();

        }
    }

    private void checkDataFounded() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("TAG", "carShop ShopProductList onDataChange:Product child exists");
                    getShopProducts();

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

    private void getShopProducts() {
        shopData = new ShopData(context);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.child("shopID").getValue().toString().equals(shopData.getId())) {
                        if (snapshot.child("categoryName").getValue().toString().equals(categoryName)) {
                            String id = snapshot.getKey();
                            String name = snapshot.child("name").getValue().toString();
                            String image = snapshot.child("image").getValue().toString();
                            String shopName = snapshot.child("shopName").getValue().toString();

                            for (DataSnapshot carSnapshot : snapshot.child("cars").getChildren()) {
                                if (carsListStore==null){
                                    carsListStore=carSnapshot.getValue().toString();

                                }
                                else {
                                    carsListStore=carsListStore+","+ carSnapshot.getValue().toString();

                                }

                            }
                            //TODO edit
                            productEntity = new ProductEntity(id, categoryName, shopData.getId(), image, name, carsListStore,shopName,"10");
                            productEntitiesList.add(productEntity);

                        } else {
                            Log.d("TAG", "carShop ShopProductList onDataChange:not founded");

                        }
                    }

                }
                    if (productEntitiesList.size() <= 0) {
                        Log.d("TAG", "carShop ShopProductList: " + productEntitiesList.size());
                        progressDialog.dismiss();
                        checkMutableLiveData.setValue("noProducts");
                    } else {
                        Log.d("TAG", "carShop ShopProductList: " + productEntitiesList.size());
                        productDao.insertProductList(productEntitiesList).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CompletableObserver() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d("TAG", "carShop ShopProductList onComplete: add data to productTable successfully");

                                        checkMutableLiveData.setValue("showDataFromRoom");

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d("TAG", "carShop onError: add data to productTable error");

                                    }
                                });

                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        }


}
