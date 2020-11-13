package com.example.carapp.user.helper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.carapp.user.ui.home.fragment.cart.CartFragmentScreen;
import com.example.carapp.user.ui.home.fragment.search.SearchFragmentScreen;
import com.example.carapp.user.ui.home.fragment.setting.SettingFragmentScreen;
import com.example.carapp.user.ui.home.fragment.shop.ShopFragmentScreen;

public class SectionPagerAdapter extends FragmentStateAdapter {
    public SectionPagerAdapter(FragmentActivity fm) {
        super(fm);
    }




    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new ShopFragmentScreen();
            case 1:
                return   new SearchFragmentScreen();


            case 2:
                return new CartFragmentScreen();

            case 3:
              return new SettingFragmentScreen();
            default:
                return null;
        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
