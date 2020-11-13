package com.example.carapp.user.ui.login.signin;

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
import com.example.carapp.Sessions.sp.TestLogin;
import com.example.carapp.Sessions.sp.UserData;
import com.example.carapp.user.ui.home.main.UserHomeScreen;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


public class UserSignInViewModel extends ViewModel {
    public MutableLiveData<String> checkSocialMutableLiveData;
    private Context context;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
    private FirebaseAuth firebaseAuth;
    private AuthCredential credential;
    private String id, name, image, userEmail;
    private UserData userData;
    private TestLogin testLogin;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 101;
    public MutableLiveData<String> checkMutableLiveData;
    private boolean emailFounded = false;
    private DatabaseReference databaseReference;


    public void checkFacebookLogin(Activity activity, CallbackManager callbackManager) {
        checkSocialMutableLiveData.setValue("");
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            Log.d("TAG", "carShop checkFacebookLogin: noInternetConnection");
            checkSocialMutableLiveData.setValue("noInternetConnection");
        } else {
            LoginManager.getInstance().setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
            LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("email"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("TAG", "carShop facebookAuth onSuccess:  success");
                    firebaseAuthWithFacebook(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d("TAG", "carShop facebookAuth onCancel: cancel");
                    checkSocialMutableLiveData.setValue("AuthOnError");
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.d("TAG", "carShop facebookAuth onError:  fail" + exception.getMessage());
                    checkSocialMutableLiveData.setValue("AuthOnError");
                }
            });
        }
    }

    private void firebaseAuthWithFacebook(AccessToken accessToken) {
        progressDialog.show();
        credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Log.d("TAG", "carShop facebookAuth onComplete: sign in with credential: success");
                    firebaseUser = firebaseAuth.getCurrentUser();


                    id = firebaseUser.getUid();

                    image = "http://graph.facebook.com/" + accessToken.getUserId() + "/picture?type=large";

                    name = firebaseUser.getDisplayName();

                    Log.d("TAG", "carShop facebookAuth onComplete: sign in with credential: success +id = " + firebaseUser.getUid());

                    testLogin.setLoginType("user");
                    userData.setName(name);
                    userData.setId(id);
                    userData.setImage(image);
                    userData.setEmail(firebaseUser.getEmail());

                    context.startActivity(new Intent(context, UserHomeScreen.class));
                    ((Activity) context).finish();


                } else {

                    Log.d("TAG", "carShop facebookAuth onComplete: sign in with credential: fail + " + task.getException());
                    progressDialog.dismiss();
                    checkSocialMutableLiveData.setValue("AuthOnError");

                }
            }
        });


    }

    public void checkGoogleLogin() {
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            Log.d("TAG", "carShop checkFacebookLogin: noInternetConnection");
            checkSocialMutableLiveData.setValue("noInternetConnection");
        } else {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            ((Activity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    public void firebaseAuthWithGoogle(String idToken) {
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG", "carShop  googleAuth onComplete: sign in with credential: success");
                    firebaseUser = firebaseAuth.getCurrentUser();
                    Log.d("TAG", "carShop  googleAuth onComplete: sign in with credential: " + firebaseUser.getPhotoUrl() + "\n" + firebaseUser.getDisplayName() + "\n" + firebaseUser.getEmail());


                    id = firebaseUser.getUid();
                    image = String.valueOf(firebaseUser.getPhotoUrl());
                    name = firebaseUser.getDisplayName();

                    Log.d("TAG", "carShop googleAuth onComplete: sign in with credential: success +id = " + firebaseUser.getUid());

                    testLogin.setLoginType("user");
                    userData.setName(name);
                    userData.setId(id);
                    userData.setImage(image);
                    userData.setEmail(firebaseUser.getEmail());

                    context.startActivity(new Intent(context, UserHomeScreen.class));
                    ((Activity) context).finish();
                } else {
                    Log.d("TAG", "carShop googleAuth onComplete: sign in with credential: fail " + task.getException());
                    progressDialog.dismiss();
                    checkSocialMutableLiveData.setValue("AuthOnError");

                }


            }
        });


    }

    public void init(Activity activity) {
        this.context = activity;
        firebaseAuth = FirebaseAuth.getInstance();
        cd = new ConnectionDetector(context);
        testLogin = new TestLogin(context);
        userData = new UserData(context);
        checkSocialMutableLiveData = new MutableLiveData<>();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(context.getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        checkMutableLiveData = new MutableLiveData<>();
    }

    public void checkData(String email, String password) {
        checkMutableLiveData.setValue("");
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            Log.d("TAG", "Shop userSignIn: noInternetConnection");
            checkMutableLiveData.setValue("noInternetConnection");
        } else {
            if (email.isEmpty() || !email.contains("@") || email.length() < 11) {
                Log.d("TAG", "carShop userSignIn: invalidEmail");
                checkMutableLiveData.setValue("invalidEmail");


            } else {
                if (password.isEmpty() || password.length() < 6) {
                    Log.d("TAG", "carShop userSignIn: invalidPassword");
                    checkMutableLiveData.setValue("invalidPassword");


                } else {
                    Log.d("TAG", "carShop userSignIn: checkDataSuccess");
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage(context.getResources().getString(R.string.loading));
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    SignIn(email, password);


                }
            }

        }


    }

    private void SignIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Log.d("TAG", "carShop userSignIn: signInSuccessfully");
                userEmail = authResult.getUser().getEmail();
                checkShopFounded(authResult.getUser().getUid());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "carShop userSignIn: signInFailed" + e);
                checkMutableLiveData.setValue("invalidEmail");
                progressDialog.dismiss();

            }
        });


    }

    private void checkShopFounded(String uid) {
        emailFounded = false;
        databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot carSnapshot : dataSnapshot.getChildren()) {

                    if (carSnapshot.getKey().equals(uid)) {
                        Log.d("TAG", "carShop userSignIn: yes");
                        emailFounded = true;

                    }

                }
                if (emailFounded) {
                    getUserData(uid);

                } else {
                    checkMutableLiveData.setValue("invalidEmail");
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "carShop userSignIn: fail to get data from database");
                checkMutableLiveData.setValue("failGetDataFromDatabase");
                progressDialog.dismiss();
            }
        });

    }

    private void getUserData(String uid) {
        databaseReference.child("User").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG", "carShop userSignIn: get data of shop from database successfully");

                testLogin = new TestLogin(context);
                testLogin.setLoginType("user");

                userData.setImage(dataSnapshot.child("image").getValue().toString());
                userData.setName(dataSnapshot.child("name").getValue().toString());
                userData.setEmail(userEmail);

                context.startActivity(new Intent(context, UserHomeScreen.class));
                ((Activity) context).finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "carShop userSignIn: fail to get data from database");
                checkMutableLiveData.setValue("failGetDataFromDatabase");
                progressDialog.dismiss();
            }
        });


    }

}