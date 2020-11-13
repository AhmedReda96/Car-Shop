package com.example.carapp.user.ui.productDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.carapp.R;
import com.example.carapp.databinding.ActivityProductDetailsScreenBinding;
import com.example.carapp.user.ui.login.signin.UserSignInViewModel;

public class ProductDetailsScreen extends AppCompatActivity implements View.OnClickListener {
    private ActivityProductDetailsScreenBinding binding;
    private ProductDetailsViewModel viewModel;
    private String productID, name, image, cars, price, shopName, categoryName;
    private int oldCount, newCount = 1;
    private float result, defaultPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_screen);

        init();
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details_screen);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);

        binding.backBtn.setOnClickListener(this);
        binding.cartBtn.setOnClickListener(this);
        binding.minusBtn.setOnClickListener(this);
        binding.plusbtn.setOnClickListener(this);

        getIntentData();
        setData();
        defaultPrice = Float.parseFloat(String.valueOf(price));
        viewModel.init(this);
        checkErrorsResult();

    }

    private void setData() {
        Glide.with(this).load(image).into(binding.productImg);
        binding.productName.setText(name);
        binding.productPrice.setText(price);
        binding.shopName.setText(shopName);
        binding.productCategory.setText(categoryName);
        binding.car.setText(cars);

    }

    private void getIntentData() {
        productID = getIntent().getStringExtra("productID");
        name = getIntent().getStringExtra("name");
        price = getIntent().getStringExtra("price");
        image = getIntent().getStringExtra("image");
        cars = getIntent().getStringExtra("cars");
        cars = getIntent().getStringExtra("cars");

        shopName = getIntent().getStringExtra("shopName");
        categoryName = getIntent().getStringExtra("categoryName");
    }

    private void checkErrorsResult() {
        viewModel.checkMutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s) {
                    case "error":
                        binding.errorTv.setText(" this item added to cart before");
                        break;

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (binding.backBtn.equals(v)) {
            onBackPressed();
        }
        if (binding.minusBtn.equals(v)) {
            oldCount = Integer.valueOf((String) binding.noOfProduct.getText());
            if (oldCount != 1) {
                newCount = oldCount - 1;
                binding.noOfProduct.setText(String.valueOf(newCount));
                result = defaultPrice * newCount;
                binding.productPrice.setText(String.valueOf(result));

            }
        }

        if (binding.plusbtn.equals(v)) {
            oldCount = Integer.valueOf((String) binding.noOfProduct.getText());
            if (oldCount >= 1) {
                newCount = oldCount + 1;
                binding.noOfProduct.setText(String.valueOf(newCount));
                result = defaultPrice * newCount;
                binding.productPrice.setText(String.valueOf(result));

            }
        }

        if (binding.cartBtn.equals(v)) {

            viewModel.addProductToCart(productID, name, shopName, categoryName, cars, binding.productPrice.getText().toString(), binding.noOfProduct.getText().toString(), image);


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
