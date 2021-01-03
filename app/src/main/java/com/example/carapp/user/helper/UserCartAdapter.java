package com.example.carapp.user.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.example.carapp.R;
import com.example.carapp.databinding.CartProductItemModelBinding;
import com.example.carapp.db.CartEntity;
import com.example.carapp.db.MainDataBase;
import com.example.carapp.db.ProductDao;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserCartAdapter extends RecyclerView.Adapter<UserCartAdapter.ViewHolder> {
    private CartProductItemModelBinding binding;
    private Context context;
    private List<CartEntity> cartEntities;
    private String cars;
    private ProductDao productDao;

    public UserCartAdapter(List<CartEntity> cartEntities, Context context) {
        this.cartEntities = cartEntities;
        this.context = context;
        productDao = MainDataBase.getInstance(context).productDao();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.cart_product_item_model, parent, false);

        return new UserCartAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CartEntity model = cartEntities.get(position);


        Glide.with(context)
                .load(model.getImage())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.binding.progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.binding.progress.setVisibility(View.GONE);
                        holder.binding.productImg.setScaleType(ImageView.ScaleType.FIT_XY);
                        holder.binding.productImg.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.productImg);

        holder.binding.productName.setText(model.getName());


        Log.d("TAG", "carShop ShopProductList : " + cars);
        binding.compatibleWith.setText(model.getCars());
        binding.price.setText(model.getPrice() + " L.E");


        holder.binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Delete");
                builder.setMessage("Are you want to delete this item?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {


                        deleteItem(model.getProductId());


                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                       dialog.dismiss();
                    }
                });


                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    private void deleteItem(String position) {


        Completable.fromAction(() -> productDao.deleteProductFromCart(position))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        notifyDataSetChanged();
                        Log.d("TAG", "carShop onComplete: delete product successfully");

                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "carShop onError: delete product fail" + e);


                    }
                });


    }


    @Override
    public int getItemCount() {
        return cartEntities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CartProductItemModelBinding binding;

        public ViewHolder(@NonNull CartProductItemModelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

}