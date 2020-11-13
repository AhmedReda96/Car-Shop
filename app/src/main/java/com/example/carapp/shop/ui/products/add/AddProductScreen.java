package com.example.carapp.shop.ui.products.add;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.carapp.R;
import com.example.carapp.databinding.ActivityAddProductScreenBinding;
import com.example.carapp.shop.ui.products.list.ProductsListScreen;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

public class AddProductScreen extends AppCompatActivity implements View.OnClickListener {
    private ActivityAddProductScreenBinding binding;
    private AddProductViewModel viewModel;
    private Uri imageUri = null;
    private List<String> carsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_screen);

        init();
    }

    private void init() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product_screen);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(AddProductViewModel.class);

        binding.backBtn.setOnClickListener(this::onClick);
        binding.saveBtn.setOnClickListener(this::onClick);
        binding.productImg.setOnClickListener(this::onClick);


        binding.mercedesCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    carsList.add("mercedes");
                }
                if (!isChecked) {
                    carsList.remove("mercedes");

                }
            }
        });
        binding.bmwCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    carsList.add("bmw");
                }
                if (!isChecked) {
                    carsList.remove("bmw");

                }
            }
        });
        binding.chevroletCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    carsList.add("chevrolet");
                }
                if (!isChecked) {
                    carsList.remove("chevrolet");
                }
            }
        });
        binding.loganCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    carsList.add("logan");
                }
                if (!isChecked) {
                    carsList.remove("logan");

                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (binding.backBtn.equals(v)) {
            onBackPressed();
        }
        if (binding.productImg.equals(v)) {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(AddProductScreen.this);
        }


        if (binding.saveBtn.equals(v)) {
            checkData();


        }
    }

    private void checkData() {
        viewModel.checkData(this, imageUri, binding.productName.getText().toString().trim(), carsList, getIntent().getStringExtra("categoryName"),binding.productPrice.getText().toString());

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
                    case "invalidCarsList":
                        binding.errorTv.setText(getResources().getString(R.string.invalidCarsList));
                        break;
                    case "invalidPrice":
                        binding.errorTv.setText(getResources().getString(R.string.invalidPrice));
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
                    binding.productImg.setPadding(0, 0, 0, 0);
                    imageUri = result.getUri();
                    Glide.with(getApplicationContext()).load(imageUri).into(binding.productImg);
                    binding.productImg.setScaleType(ImageView.ScaleType.FIT_XY);

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
        this.finish();
    }
}
