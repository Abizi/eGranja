package com.turkeytech.egranja.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.activity.MoreActivity;
import com.turkeytech.egranja.model.Product;

import java.util.ArrayList;
import java.util.Locale;

import static com.turkeytech.egranja.session.Constants.CATEGORY_HORIZONTAL_LIMIT;
import static com.turkeytech.egranja.session.Constants.PRODUCTS_CATEGORY;
import static com.turkeytech.egranja.session.Constants.PRODUCTS_NODE;

public class CategoryProductAdapter
        extends RecyclerView.Adapter<CategoryProductAdapter.CategoryViewHolder> {

    private static final String TAG = "xix: CategoryAdapter";

    private Context mContext;
    private ArrayList<String> mCategories;


    public CategoryProductAdapter(Context context, ArrayList<String> mCategories) {
        mContext = context;
        this.mCategories = mCategories;
    }

    private DatabaseReference getDatabaseReference() {
        return FirebaseDatabase
                .getInstance()
                .getReference()
                .child(PRODUCTS_NODE);
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.holder_category, parent, false);
        Log.i(TAG, "onCreateViewHolder: A view is being inflated!");
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        String categoryName = mCategories.get(position);
        holder.bindView(categoryName, mContext);
        Log.i(TAG, "onBindViewHolder: One item set!");
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: " + mCategories.size());
        return mCategories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private String mCategoryName;

        CategoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            Log.i(TAG, "CategoryViewHolder: Anybody call your name?");
        }

        void bindView(String mCategoryName, Context mContext) {
            this.mCategoryName = mCategoryName;
            setCategoryName(mCategoryName);
            setSubtitle(mCategoryName);
            setProducts(mContext);
            setMoreClick(mContext, mCategoryName);
        }

        void setCategoryName(String categoryName) {
            TextView nameText = mView.findViewById(R.id.holderCategory_txtCategory);
            nameText.setText(categoryName.toUpperCase(Locale.UK));
            Log.i(TAG, "setCategoryName: Done!");
        }

        void setSubtitle(String category) {
            TextView subtitleText = mView.findViewById(R.id.holderCategory_txtSubtitle);
            String subtitle = "Browse our " + category + " category";
            subtitleText.setText(subtitle);
            Log.i(TAG, "setSubtitle: Done!");
        }

        void setProducts(Context context) {

            final RecyclerView recyclerView = mView.findViewById(R.id.holderCategory_recyclerView);
            recyclerView.setLayoutManager(
                    new LinearLayoutManager(
                            context,
                            LinearLayoutManager.HORIZONTAL,
                            false)
            );
            setProductDataSource(recyclerView);
            Log.i(TAG, "setProducts: All Done, I hope.");
        }

        private void setProductDataSource(final RecyclerView recyclerView) {
            getDatabaseReference()
                    .orderByChild(PRODUCTS_CATEGORY)
                    .equalTo(mCategoryName)
                    .limitToFirst(CATEGORY_HORIZONTAL_LIMIT)
                    .addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ArrayList<Product> products = new ArrayList<>();
                                    Log.i(TAG, "onDataChange: New Product ArrayList created.");

                                    int i = 0;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Product product = snapshot.getValue(Product.class);
                                        product.setProductId(snapshot.getKey());
                                        if (!products.contains(product)){

                                            products.add(product);
                                        }
                                        Log.i(TAG, "onDataChange: Product added!");
                                        Log.i(TAG, "onDataChange: Product: "
                                                + products.get(i++));
                                    }
                                    recyclerView.setAdapter(new HomeProductAdapter(mContext, products, false));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.i(TAG, "onCancelled: " + databaseError.getMessage());
                                }
                            }
                    );
        }

        void setMoreClick(final Context context, final String mCategoryName){
            Button more = mView.findViewById(R.id.holderCategory_btnMore);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MoreActivity.class);
                    intent.putExtra(PRODUCTS_CATEGORY, mCategoryName);
                    context.startActivity(intent);
                }
            });
        }
    }
}
