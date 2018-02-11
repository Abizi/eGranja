package com.turkeytech.egranja.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.activity.FullScreenImageActivity;

import java.util.ArrayList;

import static com.turkeytech.egranja.session.Constants.FULL_SCREEN_IMAGE_LIST;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ProductDetailsImagesViewHolder> {

    private static final String TAG = "xix: HomeProductAdapter";
    private Context mContext;
    private ArrayList<String> images;

    public ImageAdapter(Context context, ArrayList<String> images) {

        mContext = context;
        this.images = images;

        Log.i(TAG, "HomeProductAdapter: This constructor has been called.");
    }

    @Override
    public ProductDetailsImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.holder_image_item, parent, false);
        Log.i(TAG, "onCreateViewHolder: The view is being inflated.");
        return new ProductDetailsImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductDetailsImagesViewHolder holder, int position) {

        holder.setImage(mContext, images.get(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FullScreenImageActivity.class);
                intent.putStringArrayListExtra(FULL_SCREEN_IMAGE_LIST, images);
                mContext.startActivity(intent);
            }
        });


        Log.i(TAG, "onBindViewHolder: An item has been set.");

    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: " + images.size());
        return images.size();
    }

    class ProductDetailsImagesViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ProductDetailsImagesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            Log.i(TAG, "ProductViewHolder: Anybody call your name?");
        }

        void setImage(Context context, String image) {
            ImageView imageView = mView.findViewById(R.id.holderProductDetail_image);
            Glide.with(context).load(image).into(imageView);
        }
    }
}
