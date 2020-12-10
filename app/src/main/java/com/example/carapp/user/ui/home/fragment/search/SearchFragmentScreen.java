package com.example.carapp.user.ui.home.fragment.search;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carapp.Sessions.internetcheck.ConnectionDetector;
import com.example.carapp.databinding.SearchFragmentScreenBinding;
import com.example.carapp.db.ProductEntity;
import com.example.carapp.user.helper.UserSearchAdapter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.Collections;
import java.util.List;
import static android.app.Activity.RESULT_OK;

public class SearchFragmentScreen extends Fragment implements View.OnClickListener {
    private SearchFragmentScreenBinding binding;
    private SearchFragmentViewModel viewModel;
    private UserSearchAdapter adapter;
    private Uri imageUri = null;
    private Bitmap bitmap;
    private ConnectionDetector cd;
    private Boolean isInternetPresent = false;
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
        binding.cameraBtn.setOnClickListener(this);

        binding.ivClearText.setOnClickListener(this);
        viewModel.init(getActivity());
        cd = new ConnectionDetector(getActivity());
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
        if (binding.cameraBtn.equals(v)) {
            isInternetPresent = cd.isConnectingToInternet();
            if (!isInternetPresent) {
                Log.d("TAG", "carShop  checkInternet:  !isInternetPresent");
                Toast.makeText(getActivity(),"no internet!",Toast.LENGTH_LONG).show();
            } else {
                Log.d("TAG", "carShop  checkInternet: isInternetPresent");
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(getActivity());

            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                Log.d("TAG", "onActivityResult Fragment 1: ");

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri = result.getUri();

                if (resultCode == RESULT_OK) {


                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                    viewModel.getCameraImageResult(bitmap);


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    Exception error = result.getError();
                    error.printStackTrace();
                    Log.d("TAG", "onActivityResult Fragment:  "+error.getMessage());


                }
            }


        } catch (Exception e) {

            e.printStackTrace();
        }


    }
}