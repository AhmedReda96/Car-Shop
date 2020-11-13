package com.example.carapp.shop.ui.products.add;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddProductViewModel extends ViewModel {

    public MutableLiveData<String> checkMutableLiveData = new MutableLiveData<>();
    private Context context;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private StorageReference imagePathRef;
    private HashMap<String, Object> map = new HashMap<>();
    private ShopData shopData;
    private String imageUrl, productID, categoryName;
    private Map<String, Object> carsMap = new HashMap<>();
    private ProductEntity productEntity;
    private ProductDao productDao;
    private String roomCarsList;

    public void checkData(AddProductScreen activity, Uri imageUri, String name, List<String> carsList, String categoryName, String productPrice) {
        this.categoryName = categoryName;
        checkMutableLiveData.setValue("");
        this.context = activity;
        cd = new ConnectionDetector(context);
        shopData = new ShopData(context);
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent) {
            Log.d("TAG", "Shop ShopAddProduct: noInternetConnection");
            checkMutableLiveData.setValue("noInternetConnection");
        } else {
            if (imageUri == null) {
                Log.d("TAG", "Shop ShopAddProduct: invalidImage");
                checkMutableLiveData.setValue("invalidImage");


            } else {
                if (name.isEmpty() || name.length() < 2) {
                    Log.d("TAG", "carShop ShopAddProduct: invalidName");
                    checkMutableLiveData.setValue("invalidName");


                } else {

                    if (carsList.size() == 0) {
                        Log.d("TAG", "carShop ShopAddProduct: invalidCarsList");
                        checkMutableLiveData.setValue("invalidCarsList");


                    } else {
                        if (productPrice.isEmpty() || name.length() < 1) {
                            Log.d("TAG", "carShop ShopAddProduct: invalidPrice");
                            checkMutableLiveData.setValue("invalidPrice");


                        } else {

                            progressDialog = new ProgressDialog(activity);
                            progressDialog.setMessage(activity.getResources().getString(R.string.loading));
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            uploadImage(imageUri, name, carsList,productPrice);

                        }


                    }
                }
            }

        }
    }
        private void uploadImage(Uri imageUri, String name, List<String> carsList, String productPrice){
            map.clear();
            carsMap.clear();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            productID = databaseReference.child("Product").push().getKey();


            imagePathRef = FirebaseStorage.getInstance().getReference().child("Product").child(shopData.getId()).child(productID).child(productID);
            imagePathRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("TAG", "carShop ShopAddProduct: upload Image Successfully");
                    imagePathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            imageUrl = downloadUrl.toString();
                            Log.d("TAG", "carShop ShopAddProduct  uri= " + uri.toString());
                            map.put("image", imageUrl);
                            map.put("shopID", shopData.getId());
                            map.put("shopName", shopData.getName());
                            map.put("name", name);
                            map.put("categoryName", categoryName);
                            map.put("price", productPrice);
                            for (int i = 0; i < carsList.size(); i++) {

                                carsMap.put(String.valueOf(i), carsList.get(i));
                                if (roomCarsList == null) {
                                    roomCarsList = carsList.get(i);

                                } else {
                                    roomCarsList = roomCarsList + "," + carsList.get(i);

                                }

                            }


                            map.put("cars", carsMap);

                            uploadDataToDatabase(map, imageUri, name, carsList,productPrice);
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "carShop ShopAddProduct  upload Image failed :" + e);

                }
            });


        }

        private void uploadDataToDatabase(HashMap<String, Object> map, Uri imageUri, String
                name, List<String> carsList, String productPrice){

            databaseReference.child("Product").child(productID).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {


                    Log.d("TAG", "carShop ShopAddProduct: Upload Data To Database Successfully");

                    addProductToRoom(imageUri, name, categoryName,productPrice);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "carShop ShopAddProduct: Upload Data To Database failed :" + e);
                    progressDialog.dismiss();
                }
            });


        }

        private void addProductToRoom(Uri imageUri, String name, String categoryName, String productPrice){

            productDao = MainDataBase.getInstance(context).productDao();
            productEntity = new ProductEntity(productID, categoryName, shopData.getId(), imageUrl, name, roomCarsList, shopData.getName(),productPrice);
            productDao.insertProductItem(productEntity).subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            Log.d("TAG", "carShop ShopAddProduct onComplete: add product to room successfully");
                            ((Activity) context).finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("TAG", "carShop ShopAddProduct onError: add product to room error");
                            progressDialog.dismiss();

                        }
                    });


        }

    }