package com.example.carapp.user.ui.home.fragment.shop;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carapp.databinding.ShopFragmentScreenBinding;
import com.example.carapp.db.ProductEntity;
import com.example.carapp.user.helper.UserProductsAdapter;

import java.util.Collections;
import java.util.List;

public class ShopFragmentScreen extends Fragment implements View.OnClickListener {

    private ShopFragmentViewModel viewModel;
    private ShopFragmentScreenBinding binding;
    private UserProductsAdapter adapter;

    public static ShopFragmentScreen newInstance() {
        return new ShopFragmentScreen();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = ShopFragmentScreenBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {

        viewModel = ViewModelProviders.of(this).get(ShopFragmentViewModel.class);
        binding.retryBtn.setOnClickListener(this);
        viewModel.init(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.shopRV.setLayoutManager(layoutManager);
        binding.shopRV.setHasFixedSize(true);

        viewModel.checkInternet();
        getProductsList();
        ListenOnErrors();


    }

    private void getProductsList() {

        viewModel.productsMutableLiveData.observe(getActivity(), new Observer<List<ProductEntity>>() {
            @Override
            public void onChanged(List<ProductEntity> productEntityList) {

                binding.showDataLin.setVisibility(View.VISIBLE);
                binding.connectionLin.setVisibility(View.GONE);
                binding.emptyDataLin.setVisibility(View.GONE);
                Log.d("TAG", "carShop ShopProductList getProductListFromRoom: " + productEntityList.size());

                adapter = new UserProductsAdapter(productEntityList, getActivity());
                adapter.notifyDataSetChanged();
                Collections.reverse(productEntityList);
                adapter.notifyDataSetChanged();
                binding.shopRV.setAdapter(adapter);
                viewModel.insertProductsToRoom(productEntityList);


            }
        });

    }

    private void ListenOnErrors() {

        viewModel.checkMutableLiveData.observe(getActivity(), new Observer<String>() {
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

                    case "Products":
                        binding.showDataLin.setVisibility(View.VISIBLE);
                        binding.connectionLin.setVisibility(View.GONE);
                        binding.emptyDataLin.setVisibility(View.GONE);
                        break;
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (binding.retryBtn.equals(v)) {
            viewModel.checkInternet();

        }
    }
}
