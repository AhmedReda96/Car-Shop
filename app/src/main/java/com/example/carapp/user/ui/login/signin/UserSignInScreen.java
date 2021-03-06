package com.example.carapp.user.ui.login.signin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.example.carapp.R;
import com.example.carapp.databinding.ActivityUserSigninScreenBinding;
import com.example.carapp.user.ui.login.signup.UserSignUpScreen;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserSignInScreen extends AppCompatActivity implements View.OnClickListener {
    private CallbackManager callbackManager;
    private ActivityUserSigninScreenBinding binding;
    private UserSignInViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signin_screen);
        init();

    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_signin_screen);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(UserSignInViewModel.class);


        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        binding.facebookBtn.setOnClickListener(this);
        binding.signInBtn.setOnClickListener(this);
        binding.googleBtn.setOnClickListener(this);
        binding.signUpBtn.setOnClickListener(this);
        binding.backBtn.setOnClickListener(this);

        viewModel.init(this);
        checkSocialLogin();
        Get_hash_key();
    }

    private void checkSocialLogin() {
        viewModel.checkSocialMutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s) {
                    case "noInternetConnection":
                        binding.errorTv.setText(getResources().getString(R.string.noInternetConnection));
                        break;

                    case "AuthOnError":
                        binding.errorTv.setText(getResources().getString(R.string.inValidEmail));
                        break;
                }


            }
        });


    }

    public void Get_hash_key() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.example.carapp", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.d("TAG", "Get_hash_key: "+something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }


    @Override
    public void onClick(View v) {
        if (binding.facebookBtn.equals(v)) {
            viewModel.checkFacebookLogin(this, callbackManager);
        }
        if (binding.signInBtn.equals(v)) {
            signInProcess();
        }

        if (binding.googleBtn.equals(v)) {
            viewModel.checkGoogleLogin();
        }
        if (binding.signUpBtn.equals(v)) {
            startActivity(new Intent(this, UserSignUpScreen.class));
        }
        if (binding.backBtn.equals(v)) {
            onBackPressed();
        }
    }

    private void signInProcess() {
        viewModel.checkData(binding.emailET.getText().toString().trim(), binding.passwordET.getText().toString().trim());
        viewModel.checkMutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s) {
                    case "noInternetConnection":
                        binding.errorTv.setText(getResources().getString(R.string.noInternetConnection));
                        break;

                    case "invalidEmail":
                        binding.errorTv.setText(getResources().getString(R.string.inValidEmail));
                        break;
                    case "invalidPassword":
                        binding.errorTv.setText(getResources().getString(R.string.inValidPassword));
                        break;

                    case "failGetDataFromDatabase":
                        binding.errorTv.setText(getResources().getString(R.string.failGetDataFromDatabase));
                        break;
                }
            }


        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                viewModel.firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}