package com.example.carapp.shop.ui.login.signIn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.R;
import com.example.carapp.Sessions.internetcheck.ConnectionDetector;
import com.example.carapp.Sessions.sp.ShopData;
import com.example.carapp.Sessions.sp.TestLogin;
import com.example.carapp.shop.ui.home.ShopHomeScreen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShopSignInViewModel extends ViewModel {

    public MutableLiveData<String> checkMutableLiveData = new MutableLiveData<>();
    private Context context;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private FirebaseAuth firebaseAuth;
    private TestLogin testLogin;
    private ShopData shopData;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private boolean emailFounded = false;

    public void checkData(Activity activity, String email, String password) {
        checkMutableLiveData.setValue("");
        this.context = activity;
        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            Log.d("TAG", "Shop ShopSignIn: noInternetConnection");
            checkMutableLiveData.setValue("noInternetConnection");
        } else {
            if (email.isEmpty() || !email.contains("@") || email.length() < 11) {
                Log.d("TAG", "carShop ShopSignIn: invalidEmail");
                checkMutableLiveData.setValue("invalidEmail");


            } else {
                if (password.isEmpty() || password.length() < 6) {
                    Log.d("TAG", "carShop ShopSignIn: invalidPassword");
                    checkMutableLiveData.setValue("invalidPassword");


                } else {
                    Log.d("TAG", "carShop ShopSignIn: checkDataSuccess");
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setMessage(activity.getResources().getString(R.string.loading));
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    SignIn(email, password);


                }
            }

        }


    }

    private void SignIn(String email, String password) {
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Log.d("TAG", "carShop ShopSignIn: signInSuccessfully");

                checkShopFounded(authResult.getUser().getUid());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop ShopSignIn: signInFailed" + e);
                checkMutableLiveData.setValue("invalidEmail");
                progressDialog.dismiss();

            }
        });


    }

    private void checkShopFounded(String shopId) {
        emailFounded = false;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Shop").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot carSnapshot : dataSnapshot.getChildren()) {
                    Log.d("TAG", "carShop ShopSignIn: " + carSnapshot.getKey());

                    if (carSnapshot.getKey().equals(shopId)) {
                        Log.d("TAG", "carShop ShopSignIn: yes");
                        emailFounded = true;

                    }

                }
                if (emailFounded){
                    getShopData(shopId);

                }
                else {
                    checkMutableLiveData.setValue("invalidEmail");
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "carShop ShopSignIn: fail to get data from database");
                checkMutableLiveData.setValue("failGetDataFromDatabase");
                progressDialog.dismiss();
            }
        });
    }

    private void getShopData(String shopId) {

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Shop").child(shopId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG", "carShop ShopSignIn: get data of shop from database successfully");

                testLogin = new TestLogin(context);
                testLogin.setLoginType("shop");

                shopData = new ShopData(context);
                shopData.setImage(dataSnapshot.child("image").getValue().toString());
                shopData.setName(dataSnapshot.child("name").getValue().toString());
                shopData.setId(shopId);

                context.startActivity(new Intent(context, ShopHomeScreen.class));
                ((Activity) context).finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "carShop ShopSignIn: fail to get data from database");
                checkMutableLiveData.setValue("failGetDataFromDatabase");
                progressDialog.dismiss();
            }
        });


    }

}
