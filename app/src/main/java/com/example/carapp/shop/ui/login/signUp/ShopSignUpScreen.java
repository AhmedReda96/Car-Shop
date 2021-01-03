package com.example.carapp.shop.ui.login.signUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import com.example.carapp.R;
import com.example.carapp.databinding.ActivityShopSignupScreenBinding;

public class ShopSignUpScreen extends AppCompatActivity implements View.OnClickListener {

    private ActivityShopSignupScreenBinding binding;
    private ShopSignUpViewModel viewModel;
    private Uri imageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_signup_screen);


        init();


    }

    private void init() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_signup_screen);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(ShopSignUpViewModel.class);

        binding.shopImg.setOnClickListener(this::onClick);
        binding.signUpBtn.setOnClickListener(this::onClick);
        binding.backBtn.setOnClickListener(this::onClick);


    }

    @Override
    public void onClick(View v) {

        if (binding.backBtn.equals(v)) {

            onBackPressed();
        }

        if (binding.shopImg.equals(v)) {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(ShopSignUpScreen.this);
        }

        if (binding.signUpBtn.equals(v)) {
            checkData();
        }


    }

    private void checkData() {

        viewModel.checkData(ShopSignUpScreen.this, imageUri, binding.nameET.getText().toString().trim(), binding.emailET.getText().toString().trim(), binding.passwordET.getText().toString().trim(), binding.confirmPasswordET.getText().toString().trim());

        viewModel.checkMutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {

                switch (message) {

                    case "noInternetConnection":
                        binding.errorTv.setText(getResources().getString(R.string.noInternetConnection));
                        break;
                    case "invalidImage":
                        binding.errorTv.setText(getResources().getString(R.string.emptyImage));
                        break;
                    case "invalidName":
                        binding.errorTv.setText(getResources().getString(R.string.inValidName));
                        break;
                    case "invalidEmail":
                        binding.errorTv.setText(getResources().getString(R.string.inValidEmail));
                        break;
                    case "invalidPassword":
                        binding.errorTv.setText(getResources().getString(R.string.inValidPassword));
                        break;
                    case "invalidConfirmPassword":
                        binding.errorTv.setText(getResources().getString(R.string.inValidConfirmPassword));
                        break;
                    case "authFail":
                        binding.errorTv.setText(getResources().getString(R.string.authFail));
                        break;

                }

            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        try {

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(imageReturnedIntent);

                if (resultCode == RESULT_OK) {
                    binding.shopImg.setPadding(0, 0, 0, 0);
                    imageUri = result.getUri();
                    Glide.with(getApplicationContext()).load(imageUri).into(binding.shopImg);
                    binding.shopImg.setScaleType(ImageView.ScaleType.FIT_XY);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    Exception error = result.getError();
                    error.printStackTrace();

                }
            }


        } catch (Exception e) {

            e.printStackTrace();
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

    }
}
