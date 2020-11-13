package com.example.carapp.user.ui.login.signup;

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
import com.example.carapp.Sessions.internetcheck.ConnectionDetector;
import com.example.carapp.Sessions.sp.TestLogin;
import com.example.carapp.Sessions.sp.UserData;
import com.example.carapp.user.ui.home.main.UserHomeScreen;
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

public class UserSignUpViewModel extends ViewModel {
    public MutableLiveData<String> checkMutableLiveData = new MutableLiveData<>();
    private Context context;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String userID, imageUrl, userName,userEmail;
    private StorageReference imagePathRef;
    private HashMap<String, String> map = new HashMap<>();
    private DatabaseReference databaseReference;
    private TestLogin testLogin;
    private UserData userData;

    public void init(Activity activity) {
        this.context = activity;
        cd = new ConnectionDetector(context);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        testLogin = new TestLogin(context);
        userData = new UserData(context);

    }


    public void checkData( Uri imageUri, String name, String email, String password, String confirmPassword) {
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            Log.d("TAG", "Shop UserSignUp: noInternetConnection");
            checkMutableLiveData.setValue("noInternetConnection");
        } else {
            if (imageUri == null) {
                Log.d("TAG", "Shop UserSignUp: invalidImage");
                checkMutableLiveData.setValue("invalidImage");


            } else {
                if (name.isEmpty() || name.length() < 2) {
                    Log.d("TAG", "carShop UserSignUp: invalidName");
                    checkMutableLiveData.setValue("invalidName");


                } else {
                    if (email.isEmpty() || !email.contains("@") || email.length() < 11) {
                        Log.d("TAG", "carShop UserSignUp: invalidEmail");
                        checkMutableLiveData.setValue("invalidEmail");


                    } else {
                        if (password.isEmpty() || password.length() < 6) {
                            Log.d("TAG", "carShop UserSignUp: invalidPassword");
                            checkMutableLiveData.setValue("invalidPassword");


                        } else {
                            if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
                                Log.d("TAG", "carShop UserSignUp: invalidConfirmPassword");
                                checkMutableLiveData.setValue("invalidConfirmPassword");

                            } else {
                                Log.d("TAG", "carShop UserSignUp: checkDataSuccess");
                                progressDialog = new ProgressDialog(context);
                                progressDialog.setMessage(context.getResources().getString(R.string.loading));
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
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("TAG", "carShop UserSignUp: Auth Success");
                userID = authResult.getUser().getUid();
                userEmail=authResult.getUser().getEmail();

                uploadImage(imageUri, name, userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop UserSignUp: Auth fail:" + e);
                checkMutableLiveData.setValue("authFail");
                progressDialog.dismiss();

            }
        });

    }

    private void uploadImage(Uri imageUri, String name, String userID) {
        imagePathRef = FirebaseStorage.getInstance().getReference().child("User").child(userID).child("ProfileImage").child(userID);
        imagePathRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("TAG", "carShop UserSignUp: upload Image Successfully");
                imagePathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        imageUrl = downloadUrl.toString();
                        Log.d("TAG", "carShop UserSignUp  uri= " + uri.toString());
                        map.put("image", imageUrl);
                        map.put("name", name);
                        map.put("id", userID);
                        map.put("userToken", FirebaseInstanceId.getInstance().getToken());
                        userName = name;
                        uploadDataToDatabase(map);
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop UserSignUp  upload Image failed :" + e);

            }
        });


    }

    private void uploadDataToDatabase(HashMap<String, String> map) {
        databaseReference.child("User").child(userID).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.d("TAG", "carShop UserSignUp: Upload Data To Database Successfully");
                testLogin.setLoginType("user");
                userData.setName(userName);
                userData.setId(userID);
                userData.setImage(imageUrl);
                userData.setEmail(userEmail);
                context.startActivity(new Intent(context, UserHomeScreen.class));
                ((Activity) context).finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop UserSignUp: Upload Data To Database failed :" + e);
                progressDialog.dismiss();
            }
        });



    }


}
