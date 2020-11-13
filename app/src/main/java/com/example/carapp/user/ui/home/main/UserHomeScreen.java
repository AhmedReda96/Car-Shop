package com.example.carapp.user.ui.home.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.carapp.R;
import com.example.carapp.Sessions.sp.TestLogin;
import com.example.carapp.Sessions.sp.UserData;
import com.example.carapp.databinding.ActivityUserHomeScreenBinding;
import com.example.carapp.start.StartScreen;
import com.example.carapp.user.helper.SectionPagerAdapter;
import com.facebook.login.LoginManager;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class UserHomeScreen extends AppCompatActivity {
    private ActivityUserHomeScreenBinding binding;
    private SectionPagerAdapter sectionPagerAdapter;
    private TabLayoutMediator tabLayoutMediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_screen);

        init();
    }

    private void init() {


        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_home_screen);
        binding.setLifecycleOwner(this);
        binding.viewPager.setAdapter(new SectionPagerAdapter(this));

        tabLayoutMediator = new TabLayoutMediator(binding.tab, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setIcon(R.drawable.ic_home);

                        break;

                    case 1:
                        tab.setIcon(R.drawable.ic_search);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.ic_shopping_cart);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.ic_settings);
                        break;

                }

            }
        });

        tabLayoutMediator.attach();
    }
}
