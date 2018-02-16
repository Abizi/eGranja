package com.turkeytech.egranja.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.activity.ProductDetailActivity;
import com.turkeytech.egranja.model.Product;

import java.util.ArrayList;
import java.util.List;

import static com.turkeytech.egranja.session.Constants.PRODUCT_ID;

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ProductViewHolder> {

    private static final String TAG = "xix: sm ProductAdapter";
    private Context mContext;
    private ArrayList<Product> mProducts;
    private Boolean mBigViewType;

    public HomeProductAdapter(Context context, ArrayList<Product> products, boolean bigViewType) {

        mContext = context;
        mProducts = products;
        mBigViewType = bigViewType;

        Log.i(TAG, "HomeProductAdapter: This constructor has been called.");
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        if (mBigViewType) {
            view = inflater.inflate(R.layout.holder_product_item_large, parent, false);
        } else {
            view = inflater.inflate(R.layout.holder_product_item_small, parent, false);
        }
        Log.i(TAG, "onCreateViewHolder: The view is being inflated.");
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {

        final Product product = mProducts.get(position);

        holder.setName(product.getName());
        holder.setPrice(String.valueOf(product.getPrice()));
        holder.setImage(mContext, product.getImage1());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra(PRODUCT_ID, product.getProductId());
                mContext.startActivity(intent);
            }
        });

        Log.i(TAG, "onBindViewHolder: An item has been set.");

    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: " + mProducts.size());
        return mProducts.size();
    }

    public List<Product> filter(List<Product> products, String query) {
        query = query.toLowerCase();

        ArrayList<Product> filteredProductList = new ArrayList<>();

        for (Product item : products) {
            final String productName = item.getName().toLowerCase();
            final String productPrice = String.valueOf(item.getPrice());
            if (productName.contains(query) || productPrice.contains(query)) {
                filteredProductList.add(item);
            }
        }

        return filteredProductList;

    }

    public void animateTo(List<Product> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Product> newModels) {
        for (int i = mProducts.size() - 1; i >= 0; i--) {
            final Product model = mProducts.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Product> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Product model = newModels.get(i);
            if (!mProducts.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Product> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Product model = newModels.get(toPosition);
            final int fromPosition = mProducts.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private void removeItem(int position) {
        mProducts.remove(position);
        notifyItemRemoved(position);
    }

    private void addItem(int position, Product model) {
        mProducts.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final Product model = mProducts.remove(fromPosition);
        mProducts.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            Log.i(TAG, "ProductViewHolder: Anybody call your name?");
        }

        void setName(String name) {
            TextView nameText = mView.findViewById(R.id.holderProductItem_productName);
            String nameConcat = "Name: " + name;
            nameText.setText(nameConcat);
            Log.i(TAG, "setName: Name set!");
        }

        void setPrice(String price) {
            TextView priceText = mView.findViewById(R.id.holderProductItem_productPrice);

            if (!price.contains(".")) {
                price += ".00";
            } else if(price.contains(".")){
                String[] x = price.split("\\.");
                if (x[1].length() == 1){
                    x[1] += "0";

                    price = x[0] + "." + x[1];
                }
            }

            String priceConcat = "Price: GhC " + price;
            priceText.setText(priceConcat);
            Log.i(TAG, "setPrice: Done!");
        }

        void setImage(Context context, String image) {
            ImageView imageView = mView.findViewById(R.id.holderProductItem_image);
            Glide.with(context).load(image).into(imageView);
            Log.i(TAG, "setImage: Done!");
        }
    }
}
