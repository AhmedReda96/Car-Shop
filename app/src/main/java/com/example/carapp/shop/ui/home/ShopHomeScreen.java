package com.example.carapp.shop.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.carapp.R;
import com.example.carapp.Sessions.sp.ShopData;
import com.example.carapp.Sessions.sp.TestLogin;
import com.example.carapp.databinding.ActivityShopHomeScreenBinding;
import com.example.carapp.databinding.NavHeaderBinding;
import com.example.carapp.shop.ui.products.list.ProductsListScreen;
import com.example.carapp.shop.ui.setting.SettingScreen;
import com.example.carapp.start.StartScreen;
import com.google.android.material.navigation.NavigationView;

public class ShopHomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private ActivityShopHomeScreenBinding binding;
    private ShopData shopData;
    private NavHeaderBinding headerBinding;
    private ActionBarDrawerToggle toggle;
    private TestLogin testLogin;
    private boolean doubleBackToExitPressedOnce = false;
    private Intent intent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_home_screen);
        init();


    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_home_screen);
        binding.setLifecycleOwner(this);

        testLogin = new TestLogin(this);
        shopData = new ShopData(this);


        headerBinding = headerBinding.bind(binding.navigationView.getHeaderView(0));
        binding.toolbar.setTitle("");
        binding.toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(binding.toolbar);
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(Color.BLACK);
        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        binding.navigationView.setNavigationItemSelectedListener(this);


        getDataOfNavHeader();

        binding.tailLightCat.setOnClickListener(this::onClick);
        binding.steeringWheelCat.setOnClickListener(this::onClick);
        binding.engineControlCat.setOnClickListener(this::onClick);
        binding.dashboardCat.setOnClickListener(this::onClick);
        binding.rimsCat.setOnClickListener(this::onClick);
        binding.saloonCat.setOnClickListener(this::onClick);

    }

    private void getDataOfNavHeader() {

        Glide.with(this).load(shopData.getImage()).into(headerBinding.navShopImg);
        headerBinding.shopName.setText(shopData.getName());
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_logout:

                testLogin.remove();
                shopData.remove();
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                binding.navigationView.dispatchSetSelected(true);
                startActivity(new Intent(this, StartScreen.class));
                ShopHomeScreen.this.finish();
                return true;

            case R.id.nav_aboutUs:
                // startActivity(new Intent(this, AdminMessage.class));
                // binding.drawerLayout.closeDrawer(GravityCompat.START);
                //   binding.navigationView.dispatchSetSelected(true);
                return true;

            case R.id.nav_setting:
                 startActivity(new Intent(this, SettingScreen.class));
                 binding.drawerLayout.closeDrawer(GravityCompat.START);
                 binding.navigationView.dispatchSetSelected(true);

        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        binding.navigationView.dispatchSetSelected(false);
        return true;

    }


    @Override
    protected void onResume() {
        super.onResume();
        getDataOfNavHeader();
    }

    @Override
    public void onClick(View v) {
        if (binding.tailLightCat.equals(v)) {
            intent = new Intent(new Intent(this, ProductsListScreen.class));
            intent.putExtra("categoryName", "Tail Light");
            startActivity(intent);
        }
        if (binding.steeringWheelCat.equals(v)) {
            intent = new Intent(new Intent(this, ProductsListScreen.class));
            intent.putExtra("categoryName", "Steering Wheel");
            startActivity(intent);
        }
        if (binding.engineControlCat.equals(v)) {
            intent = new Intent(new Intent(this, ProductsListScreen.class));
            intent.putExtra("categoryName", "Engine Control");
            startActivity(intent);
        }
        if (binding.dashboardCat.equals(v)) {
            intent = new Intent(new Intent(this, ProductsListScreen.class));
            intent.putExtra("categoryName", "Dashboard");
            startActivity(intent);
        }
        if (binding.saloonCat.equals(v)) {
            intent = new Intent(new Intent(this, ProductsListScreen.class));
            intent.putExtra("categoryName", "Saloon");
            startActivity(intent);
        }
        if (binding.rimsCat.equals(v)) {
            intent = new Intent(new Intent(this, ProductsListScreen.class));
            intent.putExtra("categoryName", "Rims");
            startActivity(intent);
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
