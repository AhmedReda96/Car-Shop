package com.example.carapp.shop.ui.login.signIn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.carapp.R;
import com.example.carapp.databinding.ActivityShopSigninScreenBinding;
import com.example.carapp.shop.ui.login.signUp.ShopSignUpScreen;

public class ShopSignInScreen extends AppCompatActivity implements View.OnClickListener {
    private ActivityShopSigninScreenBinding binding;
    private ShopSignInViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_signin_screen);
        init();

    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_signin_screen);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(ShopSignInViewModel.class);

        binding.signInBtn.setOnClickListener(this::onClick);
        binding.signUpBtn.setOnClickListener(this::onClick);
        binding.backBtn.setOnClickListener(this::onClick);


    }

    @Override
    public void onClick(View v) {
        if (binding.signInBtn.equals(v)) {
            viewModel.checkData(ShopSignInScreen.this, binding.emailET.getText().toString().trim(), binding.passwordET.getText().toString().trim());
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
        if (binding.signUpBtn.equals(v)) {
            startActivity(new Intent(this, ShopSignUpScreen.class));
        }
        if (binding.backBtn.equals(v)) {
            finish();
        }
    }
}
