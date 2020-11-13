package com.example.carapp.shop.ui.login.signUp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.R;
import com.example.carapp.Sessions.sp.ShopData;
import com.example.carapp.Sessions.sp.TestLogin;
import com.example.carapp.Sessions.internetcheck.ConnectionDetector;
import com.example.carapp.shop.ui.home.ShopHomeScreen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ShopSignUpViewModel extends ViewModel {
    public MutableLiveData<String> checkMutableLiveData = new MutableLiveData<>();
    private Context context;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String shopID, imageUrl, shopName;
    private StorageReference imagePathRef;
    private HashMap<String, String> map = new HashMap<>();
    private DatabaseReference databaseReference;
    private TestLogin testLogin;
    private ShopData shopData;


    public void checkData(Activity activity, Uri imageUri, String name, String email, String password, String confirmPassword) {

        checkMutableLiveData.setValue("");
        this.context = activity;
        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            Log.d("TAG", "Shop ShopSignUp: noInternetConnection");
            checkMutableLiveData.setValue("noInternetConnection");
        } else {
            if (imageUri == null) {
                Log.d("TAG", "Shop ShopSignUp: invalidImage");
                checkMutableLiveData.setValue("invalidImage");


            } else {
                if (name.isEmpty() || name.length() < 2) {
                    Log.d("TAG", "carShop ShopSignUp: invalidName");
                    checkMutableLiveData.setValue("invalidName");


                } else {
                    if (email.isEmpty() || !email.contains("@") || email.length() < 11) {
                        Log.d("TAG", "carShop ShopSignUp: invalidEmail");
                        checkMutableLiveData.setValue("invalidEmail");


                    } else {
                        if (password.isEmpty() || password.length() < 6) {
                            Log.d("TAG", "carShop ShopSignUp: invalidPassword");
                            checkMutableLiveData.setValue("invalidPassword");


                        } else {
                            if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
                                Log.d("TAG", "carShop ShopSignUp: invalidConfirmPassword");
                                checkMutableLiveData.setValue("invalidConfirmPassword");

                            } else {
                                Log.d("TAG", "carShop ShopSignUp: checkDataSuccess");
                                progressDialog = new ProgressDialog(activity);
                                progressDialog.setMessage(activity.getResources().getString(R.string.loading));
                                progressDialog.setCancelable(true);
                                progressDialog.show();
                                signUp(imageUri, name, email, password);

                            }
                        }
                    }
                }
            }


        }
    }

    private void signUp(Uri imageUri, String name, String email, String password) {

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("TAG", "carShop ShopSignUp: Auth Success");
                shopID = authResult.getUser().getUid();
                uploadImage(imageUri, name, shopID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop ShopSignUp: Auth fail:" + e);
                checkMutableLiveData.setValue("authFail");
                progressDialog.dismiss();

            }
        });


    }

    private void uploadImage(Uri imageUri, String name, String shopID) {
        imagePathRef = FirebaseStorage.getInstance().getReference().child("Shop").child(shopID).child("ProfileImage").child(shopID);
        imagePathRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("TAG", "carShop ShopSignUp: upload Image Successfully");
                imagePathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        imageUrl = downloadUrl.toString();
                        Log.d("TAG", "carShop ShopSignUp  uri= " + uri.toString());
                        map.put("image", imageUrl);
                        map.put("name", name);
                        map.put("id", shopID);
                        map.put("adminToken", FirebaseInstanceId.getInstance().getToken());
                        shopName = name;
                        uploadDataToDatabase(map);
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop ShopSignUp  upload Image failed :" + e);

            }
        });


    }

    private void uploadDataToDatabase(HashMap<String, String> map) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Shop").child(shopID).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                testLogin = new TestLogin(context);
                shopData = new ShopData(context);

                Log.d("TAG", "carShop ShopSignUp: Upload Data To Database Successfully");
                testLogin.setLoginType("shop");
                shopData.setName(shopName);
                shopData.setId(shopID);
                shopData.setImage(imageUrl);
                context.startActivity(new Intent(context, ShopHomeScreen.class));
                ((Activity) context).finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop ShopSignUp: Upload Data To Database failed :" + e);
                progressDialog.dismiss();
            }
        });


    }


}