package com.example.carapp.user.ui.home.fragment.cart;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carapp.databinding.CartFragmentScreenBinding;
import com.example.carapp.db.CartEntity;
import com.example.carapp.user.helper.UserCartAdapter;

import java.util.Collections;
import java.util.List;

public class CartFragmentScreen extends Fragment implements View.OnClickListener {
    private CartFragmentScreenBinding binding;
    private CartFragmentViewModel viewModel;
    private UserCartAdapter adapter;


    public static CartFragmentScreen newInstance() {
        return new CartFragmentScreen();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = CartFragmentScreenBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        viewModel = ViewModelProviders.of(this).get(CartFragmentViewModel.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.cartRV.setLayoutManager(layoutManager);
        binding.cartRV.setHasFixedSize(true);
        binding.checkoutBtn.setOnClickListener(this);
        viewModel.init(getActivity());
        getCartResult();
        viewModel.getProductData();

    }

    private void getCartResult() {

        viewModel.cartLiveData.observe(getActivity(), new Observer<List<CartEntity>>() {
            @Override
            public void onChanged(List<CartEntity> cartEntities) {
                adapter = new UserCartAdapter(cartEntities, getActivity());
                adapter.notifyDataSetChanged();
                Collections.reverse(cartEntities);
                adapter.notifyDataSetChanged();
                binding.cartRV.setAdapter(adapter);
            }
        });


    }


    @Override
    public void onClick(View v) {
        if (binding.checkoutBtn.equals(v)) {
            viewModel.checkout();
        }
    }
}
