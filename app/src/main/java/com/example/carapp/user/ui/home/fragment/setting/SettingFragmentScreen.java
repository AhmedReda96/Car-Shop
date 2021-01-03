package com.example.carapp.user.ui.home.fragment.setting;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.carapp.Sessions.sp.TestLogin;
import com.example.carapp.Sessions.sp.UserData;

import com.example.carapp.databinding.SettingFragmentScreenBinding;
import com.example.carapp.start.StartScreen;
import com.google.firebase.auth.FirebaseAuth;

public class SettingFragmentScreen extends Fragment {
    private SettingFragmentScreenBinding binding;
    private SettingFragmentViewModel mViewModel;
    private UserData userData;
    private TestLogin testLogin;

    public static SettingFragmentScreen newInstance() {
        return new SettingFragmentScreen();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = SettingFragmentScreenBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mViewModel = ViewModelProviders.of(this).get(SettingFragmentViewModel.class);
        userData = new UserData(getActivity());
        testLogin = new TestLogin(getActivity());

        setData();
    }

    private void setData() {


        binding.userName.setText(userData.getName());
        binding.userEmail.setText(userData.getEmail());

        Glide.with(this).load(userData.getImage()).into(binding.userImg);


        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                testLogin.remove();
                userData.remove();
                startActivity(new Intent(getActivity(), StartScreen.class));
                getActivity().finish();
            }
        });


    }


}
