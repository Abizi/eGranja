package com.turkeytech.egranja.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.activity.EditProductActivity;
import com.turkeytech.egranja.activity.HomeActivity;
import com.turkeytech.egranja.model.Product;

import java.util.ArrayList;

import static com.turkeytech.egranja.session.Constants.CATEGORIES_NODE;
import static com.turkeytech.egranja.session.Constants.HOME_SCREEN;
import static com.turkeytech.egranja.session.Constants.NAV_PRODUCTS;
import static com.turkeytech.egranja.session.Constants.PRODUCTS_NODE;
import static com.turkeytech.egranja.session.Constants.PRODUCT_ID;

public class ListProductAdapter
        extends RecyclerView.Adapter<ListProductAdapter.UserProductViewHolder> {

    private static final String TAG = "xix: ListProductAdapter";

    private Context mContext;
    private ArrayList<Product> mProducts;


    public ListProductAdapter(Context context, ArrayList<Product> mProducts) {
        mContext = context;
        this.mProducts = mProducts;
    }

    @Override
    public UserProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.holder_user_product, parent, false);
        Log.i(TAG, "onCreateViewHolder: A view is being inflated!");
        return new UserProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserProductViewHolder holder, int position) {
        holder.bindView(mProducts.get(position), mContext);

        Log.i(TAG, "onBindViewHolder: One item set!");
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: " + mProducts.size());
        return mProducts.size();
    }

    class UserProductViewHolder extends RecyclerView.ViewHolder {

        View mView;

        UserProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            Log.i(TAG, "UserProductViewHolder: Anybody call your name?");
        }

        void bindView(Product mProduct, Context mContext) {
            setProductName(mProduct.getName());
            setProductPrice(String.valueOf(mProduct.getPrice()));
            setProductQuantity(mProduct.getQuantity());
            setProductCategory(mProduct.getCategory());
            setImage(mProduct.getImage1(), mContext);
            setOnMoreClick(mProduct);
//            setOnClick(mProduct);
        }

        void setProductName(String name) {
            TextView nameText = mView.findViewById(R.id.holderUserProduct_productName);
            nameText.setText(name);
            Log.i(TAG, "setProductName: Done!");
        }

        void setProductPrice(String price) {
            TextView priceText = mView.findViewById(R.id.holderUserProduct_productPrice);
            priceText.setText(price);
            Log.i(TAG, "setProductPrice: Done!");
        }

        void setProductQuantity(String quantity) {
            TextView quantityText = mView.findViewById(R.id.holderUserProduct_productQuantity);
            quantityText.setText(quantity);
            Log.i(TAG, "setProductQuantity: Done!");
        }

        void setProductCategory(String category) {
            TextView categoryText = mView.findViewById(R.id.holderUserProduct_productCategory);
            categoryText.setText(category);
            Log.i(TAG, "setProductCategory: Done!");
        }

        void setImage(String imageLocation, Context context) {
            ImageView imageView = mView.findViewById(R.id.holderUserProduct_image);
            Glide.with(context).load(imageLocation).into(imageView);
            Log.i(TAG, "setImage: Done!");
        }

        void setOnMoreClick(final Product product) {
            ImageButton moreButton = mView.findViewById(R.id.holderUserProduct_btnMore);
            moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu popupMenu = new PopupMenu(mContext, view);
                    popupMenu.getMenuInflater().inflate(R.menu.user_product_menu, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_delete_product:
                                    DatabaseReference ref = FirebaseDatabase
                                            .getInstance()
                                            .getReference();
                                    ref.child(PRODUCTS_NODE)
                                            .child(product.getProductId())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent intent = new Intent(mContext, HomeActivity.class);
                                            intent.putExtra(HOME_SCREEN, NAV_PRODUCTS);
                                            mContext.startActivity(intent);
                                        }
                                    });
                                    ref.child(CATEGORIES_NODE).child(product.getCategory()).child(product.getProductId()).removeValue();
                                    break;
                                case R.id.action_edit_product:
                                    Intent intent = new Intent(mContext, EditProductActivity.class);
                                    intent.putExtra(PRODUCT_ID, product.getProductId());
                                    mContext.startActivity(intent);
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        /*private void setOnClick(final Product product) {
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ProductDetailActivity.class);
                    intent.putExtra(PRODUCT_ID, product.getProductId());
                    mContext.startActivity(intent);
                }
            });
        }*/

    }
}
