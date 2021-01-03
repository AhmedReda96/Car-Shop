package com.example.carapp.shop.ui.products.edit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.carapp.R;
import com.example.carapp.databinding.ActivityEditProductScreenBinding;

public class EditProductScreen extends AppCompatActivity implements View.OnClickListener {

    private ActivityEditProductScreenBinding binding;
    private EditProductViewModel viewModel;
    private String productId, productName, productImage, cars, price;
    private Uri imageUri = null;
    private List<String> carsList = new ArrayList<>();
    private List<String> newCarsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_screen);

        init();
    }

    private void init() {


        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_product_screen);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(EditProductViewModel.class);


        binding.backBtn.setOnClickListener(this::onClick);
        binding.saveBtn.setOnClickListener(this::onClick);
        binding.addImg.setOnClickListener(this::onClick);
        binding.deleteBtn.setOnClickListener(this::onClick);


        getIntentData();
        setData();

    }

    private void setData() {
        Glide.with(this).load(productImage).into(binding.productImg);
        binding.productName.setText(productName);
        binding.productPrice.setText(price);

        for (int i = 0; i < carsList.size(); i++) {
            if (carsList.get(i).equals("chevrolet")) {

                binding.chevroletCB.setChecked(true);

            }
            if (carsList.get(i).equals("bmw")) {

                binding.bmwCB.setChecked(true);

            }
            if (carsList.get(i).equals("mercedes")) {

                binding.mercedesCB.setChecked(true);

            }
            if (carsList.get(i).equals("logan")) {

                binding.loganCB.setChecked(true);

            }


        }

    }

    private void getIntentData() {
        productId = getIntent().getStringExtra("productID");
        productName = getIntent().getStringExtra("name");
        price = getIntent().getStringExtra("price");
        productImage = getIntent().getStringExtra("image");
        cars = getIntent().getStringExtra("cars");

        Log.d("TAG", "carShop edit: " + cars);

        carsList = (Arrays.asList(cars.split(",")));

        for (int i = 0; i < carsList.size(); i++) {
            Log.d("TAG", "carShop carsList: " + carsList.get(i));
        }


    }

    @Override
    public void onClick(View v) {
        if (binding.backBtn.equals(v)) {

            onBackPressed();
        }
        if (binding.deleteBtn.equals(v)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure to delete this product?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    viewModel.deleteProduct(productId, EditProductScreen.this);

                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();


        }
        if (binding.saveBtn.equals(v)) {
            checkData();

        }
        if (binding.addImg.equals(v)) {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(EditProductScreen.this);

        }
    }

    private void checkData() {
        if (binding.mercedesCB.isChecked()) {

            newCarsList.add("mercedes");
        }
        if (binding.bmwCB.isChecked()) {

            newCarsList.add("bmw");
        }
        if (binding.loganCB.isChecked()) {

            newCarsList.add("logan");
        }
        if (binding.chevroletCB.isChecked()) {

            newCarsList.add("chevrolet");
        }


        viewModel.checkData(productId, this, imageUri, binding.productName.getText().toString().trim(), newCarsList,binding.productPrice.getText().toString());

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
                    case "errorDelete":
                        binding.errorTv.setText(getResources().getString(R.string.errorDelete));
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
        finish();
    }
}
