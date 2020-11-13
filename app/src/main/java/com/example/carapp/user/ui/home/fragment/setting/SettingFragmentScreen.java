package com.example.carapp.user.ui.home.fragment.setting;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carapp.R;

public class SettingFragmentScreen extends Fragment {

    private SettingFragmentViewModel mViewModel;

    public static SettingFragmentScreen newInstance() {
        return new SettingFragmentScreen();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_fragment_screen, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SettingFragmentViewModel.class);
        // TODO: Use the ViewModel
    }


       /*   UserData userData = new UserData(this);
        binding.name.setText(userData.getName());
        binding.email.setText(userData.getEmail());

        TestLogin testLogin = new TestLogin(this);

        Glide.with(this).load(userData.getImage()).into(binding.img);


        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                testLogin.remove();
                userData.remove();
                startActivity(new Intent(UserHomeScreen.this, StartScreen.class));
                finish();
            }
        });

      */
}
