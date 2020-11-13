package com.example.carapp.user.ui.home.fragment.search;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.db.MainDataBase;
import com.example.carapp.db.ProductDao;
import com.example.carapp.db.ProductEntity;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchFragmentViewModel extends ViewModel {
    private Context context;
    ProductDao productDao;
    public MutableLiveData<List<ProductEntity>> searchLiveData;


    public void init(Activity activity) {
        this.context = activity;
        productDao = MainDataBase.getInstance(context).productDao();
        searchLiveData = new MutableLiveData<>();
    }


    public void getSearchData(String query) {
        Log.d("Tag", "carShop search  query:  " + query);

        productDao.searchProduct(query).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ProductEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("TAG", "carShop ShopProductList onSubscribe: ");

                    }

                    @Override
                    public void onNext(List<ProductEntity> productEntityList) {
                            Log.d("TAG", "carShop search onNext: " +productEntityList.size());

                            searchLiveData.setValue(productEntityList);


                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d("TAG", "carShop search onError: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.d("Tag", "carShop search : onComplete get research data ");

                    }
                });














    }
}
