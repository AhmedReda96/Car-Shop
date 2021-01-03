package com.example.carapp.user.ui.home.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import com.example.carapp.R;
import com.example.carapp.databinding.ActivityUserHomeScreenBinding;
import com.example.carapp.user.helper.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class UserHomeScreen extends AppCompatActivity {
    private ActivityUserHomeScreenBinding binding;
    private SectionPagerAdapter sectionPagerAdapter;
    private TabLayoutMediator tabLayoutMediator;
    private boolean doubleBackToExitPressedOnce = false;


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.finishAffinity();

            return;
        }

        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}