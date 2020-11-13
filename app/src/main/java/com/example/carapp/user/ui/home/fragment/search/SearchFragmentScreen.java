package com.example.carapp.user.ui.home.fragment.search;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carapp.R;
import com.example.carapp.databinding.SearchFragmentScreenBinding;
import com.example.carapp.db.ProductEntity;
import com.example.carapp.user.helper.UserSearchAdapter;

import java.util.Collections;
import java.util.List;

public class SearchFragmentScreen extends Fragment implements View.OnClickListener {
    private SearchFragmentScreenBinding binding;
    private SearchFragmentViewModel viewModel;
    private UserSearchAdapter adapter;

    public static SearchFragmentScreen newInstance() {
        return new SearchFragmentScreen();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = SearchFragmentScreenBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        getSearchResult();
    }

    private void getSearchResult() {
        viewModel.searchLiveData.observe(getActivity(), new Observer<List<ProductEntity>>() {
            @Override
            public void onChanged(List<ProductEntity> productEntities) {
                Log.d("TAG", "carShop search list:" + productEntities.size());


                adapter = new UserSearchAdapter(productEntities, getActivity());
                adapter.notifyDataSetChanged();
                Collections.reverse(productEntities);
                adapter.notifyDataSetChanged();
                binding.searchRV.setAdapter(adapter);
                binding.searchRV.setVisibility(View.VISIBLE);

            }
        });
    }

    private void init() {

        viewModel = ViewModelProviders.of(this).get(SearchFragmentViewModel.class);

        binding.ivClearText.setOnClickListener(this);
        viewModel.init(getActivity());

        binding.edtSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    binding.searchRV.setVisibility(View.GONE);
                    binding.ivClearText.setVisibility(View.GONE);
                } else {

                    binding.ivClearText.setVisibility(View.VISIBLE);
                    viewModel.getSearchData(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.searchRV.setLayoutManager(layoutManager);
        binding.searchRV.setHasFixedSize(true);


    }

    @Override
    public void onClick(View v) {
        if (binding.ivClearText.equals(v)) {
            binding.edtSearchText.setText("");
            binding.ivClearText.setVisibility(View.GONE);
            binding.searchRV.setVisibility(View.GONE);


        }
    }
}
