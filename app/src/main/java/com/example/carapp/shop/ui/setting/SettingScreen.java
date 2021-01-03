package com.example.carapp.shop.ui.setting;

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

import com.example.carapp.R;
import com.example.carapp.Sessions.sp.ShopData;
import com.example.carapp.databinding.ActivitySettingScreenBinding;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SettingScreen extends AppCompatActivity implements View.OnClickListener {
    private ActivitySettingScreenBinding binding;
    private SettingViewModel viewModel;
    private ShopData shopData;
    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_screen);

        init();
    }

    private void init() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_screen);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        shopData = new ShopData(this);
        binding.backBtn.setOnClickListener(this::onClick);
        binding.addImg.setOnClickListener(this::onClick);
        binding.saveBtn.setOnClickListener(this::onClick);
        getShopData();
    }

    private void getShopData() {
        Glide.with(this).load(shopData.getImage()).into(binding.shopImg);
        binding.shopName.setText(shopData.getName());

    }

    @Override
    public void onClick(View v) {
        if (binding.saveBtn.equals(v)) {
            checkData();


        }
        if (binding.backBtn.equals(v)) {

            onBackPressed();
        }
        if (binding.addImg.equals(v)) {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(SettingScreen.this);

        }
    }

    private void checkData() {

        viewModel.checkData(shopData.getId(), this, imageUri, binding.shopName.getText().toString().trim());

        viewModel.checkMutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                switch (message) {
                    case "noInternetConnection":
                        binding.errorTv.setText(getResources().getString(R.string.noInternetConnection));
                        break;
                    case "success":
                        finish();
                        break;
                    case "invalidName":
                        binding.errorTv.setText(getResources().getString(R.string.inValidName));
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
