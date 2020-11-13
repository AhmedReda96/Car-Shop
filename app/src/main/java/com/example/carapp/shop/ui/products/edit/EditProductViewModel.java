package com.example.carapp.shop.ui.products.edit;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class EditProductViewModel extends ViewModel {
    public MutableLiveData<String> checkMutableLiveData = new MutableLiveData<>();
    private Context context;
    private ConnectionDetector cd;
    private ShopData shopData;
    private Boolean isInternetPresent = false;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private ProductDao productDao;
    private StorageReference imgRef;
    private String productID, imageUrl, productName,productPrice;
    private HashMap<String, Object> map = new HashMap<>();
    private Map<String, Object> carsMap = new HashMap<>();
    private String roomCarsList;
    private Uri productUri;

    public void deleteProduct(String productId, EditProductScreen activity) {
        this.productID = productId;
        checkMutableLiveData.setValue("");
        this.context = activity;
        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();


        if (!isInternetPresent) {
            Log.d("TAG", "carShop EditProduct: noInternetConnection");
            checkMutableLiveData.setValue("noInternetConnection");
        } else {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(activity.getResources().getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Product").child(productId);
            databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Log.d("TAG", "carShop EditProduct: remove product successfully");
                    deleteImageFromStorage(productId);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "carShop EditProduct: remove product fail");
                    checkMutableLiveData.setValue("errorDelete");
                    progressDialog.dismiss();
                }
            });
        }


    }

    private void deleteImageFromStorage(String productId) {
        shopData = new ShopData(context);
        imgRef = FirebaseStorage.getInstance().getReference().child("Product").child(shopData.getId()).child(productId).child(productId);
        imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "carShop EditProduct: remove product image successfully");
                deleteProductFromRoom(productId);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop EditProduct: remove product image fail");
                checkMutableLiveData.setValue("errorDelete");
                progressDialog.dismiss();

            }
        });


    }

    private void deleteProductFromRoom(String productId) {

        productDao = MainDataBase.getInstance(context).productDao();
        productDao.deleteProductData(productId).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "carShop ShopAddProduct EditProduct onComplete: delete product to room successfully");
                        ((Activity) context).finish();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", "carShop ShopAddProduct EditProduct onError: add product to room error");
                        progressDialog.dismiss();

                    }
                });

    }

    public void checkData(String productId, EditProductScreen activity, Uri imageUri, String name, List<String> carsList, String price) {
        roomCarsList = null;
        this.productID = productId;
        this.productName = name;
        this.productPrice = price;
        this.productUri = imageUri;
        checkMutableLiveData.setValue("");
        this.context = activity;
        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();
        shopData = new ShopData(context);

        if (!isInternetPresent) {
            Log.d("TAG", "Shop EditProduct: noInternetConnection");
            checkMutableLiveData.setValue("noInternetConnection");
        } else {
            if (name.isEmpty() || name.length() < 2) {
                Log.d("TAG", "carShop EditProduct: invalidName");
                checkMutableLiveData.setValue("invalidName");


            } else {

                if (carsList.size() == 0) {
                    Log.d("TAG", "carShop EditProduct: invalidCarsList");
                    checkMutableLiveData.setValue("invalidCarsList");


                } else {
                    if (price.isEmpty() || name.length() < 1) {
                        Log.d("TAG", "carShop EditProduct: invalidPrice");
                        checkMutableLiveData.setValue("invalidPrice");


                    } else {

                        progressDialog = new ProgressDialog(activity);
                        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
                        progressDialog.setCancelable(true);
                        progressDialog.show();


                        if (imageUri == null) {


                            updateDataWithoutImage(carsList);


                        } else {
                            replaceOldImage(carsList);


                        }


                    }

                }
            }
        }
    }

    private void updateDataWithoutImage(List<String> carsList) {
        map.clear();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product").child(productID);
        map.put("name", productName);
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

        databaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "carShop ShopAddProduct  update data Successfully");
                progressDialog.dismiss();


                updateRoomWithoutImage();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop ShopAddProduct  update data failed :" + e);

                progressDialog.dismiss();

            }
        });


    }

    private void updateRoomWithoutImage() {

        productDao = MainDataBase.getInstance(context).productDao();
        productDao.updateProductWithoutImage(productID, productName, roomCarsList,productPrice).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "carShop ShopAddProduct onComplete: update product to room successfully");
                        ((Activity) context).finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", "carShop ShopAddProduct onError: update product to room error::" + e.getMessage());
                        progressDialog.dismiss();

                    }
                });


    }

    private void replaceOldImage(List<String> carsList) {

        imgRef = FirebaseStorage.getInstance().getReference().child("Product").child(shopData.getId()).child(this.productID).child(this.productID);
        imgRef.putFile(productUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("TAG", "carShop ShopAddProduct: upload Image Successfully");
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        imageUrl = downloadUrl.toString();
                        Log.d("TAG", "carShop ShopAddProduct  uri= " + uri.toString());

                        updateDataWithImage(carsList);

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

    private void updateDataWithImage(List<String> carsList) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product").child(productID);
        map.put("name", productName);
        map.put("image", imageUrl);
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

        databaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "carShop ShopAddProduct  update data Successfully");
                progressDialog.dismiss();


                updateRoomWithImage();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop ShopAddProduct  update data failed :" + e);

                progressDialog.dismiss();

            }
        });


    }

    private void updateRoomWithImage() {

        productDao = MainDataBase.getInstance(context).productDao();
        productDao.updateProductWithImage(productID, imageUrl, productName, roomCarsList,productPrice).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "carShop ShopAddProduct onComplete: update product to room successfully");
                        ((Activity) context).finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TAG", "carShop ShopAddProduct onError: update product to room error::" + e.getMessage());
                        progressDialog.dismiss();

                    }
                });


    }


}