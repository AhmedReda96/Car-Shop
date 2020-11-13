package com.example.carapp.user.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.example.carapp.Sessions.sp.TestLogin;
import com.example.carapp.databinding.UserProductItemModelBinding;
import com.example.carapp.db.ProductEntity;
import com.example.carapp.user.ui.productDetails.ProductDetailsScreen;

import java.util.List;

public class UserProductsAdapter extends RecyclerView.Adapter<UserProductsAdapter.ViewHolder> {
    private UserProductItemModelBinding binding;
    private Context context;
    private List<ProductEntity> productEntities;
    private String cars;
    private TestLogin testLogin;

    public UserProductsAdapter(List<ProductEntity> productEntityList, Context context) {
        this.productEntities = productEntityList;
        this.context = context;
        testLogin = new TestLogin(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.user_product_item_model, parent, false);

        return new UserProductsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ProductEntity model = productEntities.get(position);


        Glide.with(context)
                .load(model.getImage())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.binding.menuImgProgress.setVisibility(View.GONE);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.binding.menuImgProgress.setVisibility(View.GONE);
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
        binding.price.setText(model.getPrice()+" L.E");

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsScreen.class);
                intent.putExtra("productID", String.valueOf(model.getProductId()));
                intent.putExtra("name", String.valueOf(model.getName()));
                intent.putExtra("image", String.valueOf(model.getImage()));
                intent.putExtra("shopName", String.valueOf(model.getShopName()));
                intent.putExtra("categoryName", String.valueOf(model.getCategoryName()));
                intent.putExtra("price", String.valueOf(model.getPrice()));
                intent.putExtra("cars", model.getCars());
                context.startActivity(intent);


            }
        });
    }


    @Override
    public int getItemCount() {
        return productEntities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        UserProductItemModelBinding binding;

        public ViewHolder(@NonNull UserProductItemModelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

}