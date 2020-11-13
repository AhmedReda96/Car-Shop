package com.example.carapp.shop.ui.setting;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SettingViewModel extends ViewModel {

    public MutableLiveData<String> checkMutableLiveData = new MutableLiveData<>();
    private Context context;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private StorageReference imgRef;
    private HashMap<String, Object> map = new HashMap<>();
    private Uri shopUri;
    private String imageUrl, shopID, shopName;
    private ShopData shopData;

    public void checkData(String id, SettingScreen activity, Uri imageUri, String name) {

        this.shopID = id;
        this.shopName = name;
        this.shopUri = imageUri;
        checkMutableLiveData.setValue("");
        this.context = activity;
        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();

        shopData = new ShopData(context);

        if (!isInternetPresent) {
            Log.d("TAG", "carShop ShopSetting: noInternetConnection");
            checkMutableLiveData.setValue("noInternetConnection");
        } else {
            if (name.isEmpty() || name.length() < 2) {
                Log.d("TAG", "carShop ShopSetting: invalidName");
                checkMutableLiveData.setValue("invalidName");


            } else {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage(activity.getResources().getString(R.string.loading));
                progressDialog.setCancelable(true);
                progressDialog.show();


                if (imageUri == null) {


                    updateDataWithoutImage();


                } else {
                    replaceOldImage();


                }

            }
        }

    }

    private void replaceOldImage() {
        imgRef = FirebaseStorage.getInstance().getReference().child("Shop").child(shopID).child("ProfileImage").child(shopID);
        imgRef.putFile(shopUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("TAG", "carShop ShopSetting: upload Image Successfully");
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        imageUrl = downloadUrl.toString();
                        Log.d("TAG", "carShop ShopSetting  uri= " + uri.toString());

                        updateDataWithImage();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop ShopSetting  upload Image failed :" + e);

            }
        });


    }

    private void updateDataWithImage() {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Shop").child(shopID);
        map.put("name", shopName);
        map.put("image", imageUrl);

        databaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "carShop ShopSetting  update data Successfully");
                progressDialog.dismiss();


                updateSPWithImage();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop ShopSetting  update data failed :" + e);

                progressDialog.dismiss();

            }
        });


    }

    private void updateSPWithImage() {

        shopData.setName(shopName);
        shopData.setImage(imageUrl);
        ((Activity) context).finish();



    }

    private void updateDataWithoutImage() {
        map.clear();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Shop").child(shopID);
        map.put("name", shopName);
        databaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "carShop ShopSetting  update shop data Successfully");
                progressDialog.dismiss();

                updateSPWithoutImage();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop ShopSetting  update shop data failed :" + e);

                progressDialog.dismiss();

            }
        });


    }

    private void updateSPWithoutImage() {
        shopData.setName(shopName);
        ((Activity) context).finish();

    }


}
