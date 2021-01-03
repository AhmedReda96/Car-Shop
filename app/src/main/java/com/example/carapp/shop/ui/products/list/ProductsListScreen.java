package com.example.carapp.shop.ui.products.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.carapp.R;
import com.example.carapp.databinding.ActivityProductsListScreenBinding;
import com.example.carapp.db.ProductEntity;
import com.example.carapp.shop.helper.ProductsAdapter;
import com.example.carapp.shop.ui.products.add.AddProductScreen;

import java.util.Collections;
import java.util.List;

public class ProductsListScreen extends AppCompatActivity implements View.OnClickListener {
    private ActivityProductsListScreenBinding binding;
    private Intent intent;
    private ProductsListViewModel viewModel;
    private ProductsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list_screen);
        init();
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_products_list_screen);
        binding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(this).get(ProductsListViewModel.class);

        binding.backBtn.setOnClickListener(this::onClick);
        binding.addCenterProductBtn.setOnClickListener(this::onClick);
        binding.addCornerProductBtn.setOnClickListener(this::onClick);
        binding.retryBtn.setOnClickListener(this::onClick);

        binding.categoryName.setText(getIntent().getStringExtra("categoryName"));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.ProductRV.setLayoutManager(layoutManager);
        binding.ProductRV.setHasFixedSize(true);


        getProductsListFromRoom();
        ListenOnErrors();
    }

    private void ListenOnErrors() {

        viewModel.checkMutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                switch (message) {
                    case "noInternetConnection":
                        binding.connectionLin.setVisibility(View.VISIBLE);
                        binding.emptyDataLin.setVisibility(View.GONE);
                        binding.showDataLin.setVisibility(View.GONE);

                        break;
                    case "noProducts":
                        binding.showDataLin.setVisibility(View.GONE);

                        binding.connectionLin.setVisibility(View.GONE);
                        binding.emptyDataLin.setVisibility(View.VISIBLE);
                        break;
                    case "showDataFromRoom":
                        getProductsListFromRoom();
                        break;
                    case "invalidCarsList":

                        break;
                }
            }
        });

    }

    private void getProductsListFromRoom() {

        viewModel.getProductsListFromRoom(getIntent().getStringExtra("categoryName"), this);
        viewModel.productsMutableLiveData.observe(this, new Observer<List<ProductEntity>>() {
            @Override
            public void onChanged(List<ProductEntity> productEntityList) {

                binding.showDataLin.setVisibility(View.VISIBLE);
                binding.connectionLin.setVisibility(View.GONE);
                binding.emptyDataLin.setVisibility(View.GONE);
                Log.d("TAG", "carShop ShopProductList getProductListFromRoom: " + productEntityList.size());
                adapter = new ProductsAdapter(productEntityList, ProductsListScreen.this);
                adapter.notifyDataSetChanged();
                Collections.reverse(productEntityList);
                adapter.notifyDataSetChanged();
                binding.ProductRV.setAdapter(adapter);

            }
        });


    }

    @Override
    public void onClick(View v) {
        if (binding.backBtn.equals(v)) {
            onBackPressed();
        }
        if (binding.retryBtn.equals(v)) {
            getProductsListFromRoom();
        }if (binding.addCenterProductBtn.equals(v)) {
            intent = new Intent(this, AddProductScreen.class);
            intent.putExtra("categoryName", getIntent().getStringExtra("categoryName"));
            startActivity(intent);
        }




        if (binding.addCornerProductBtn.equals(v)) {
            intent = new Intent(this, AddProductScreen.class);
            intent.putExtra("categoryName", getIntent().getStringExtra("categoryName"));
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
