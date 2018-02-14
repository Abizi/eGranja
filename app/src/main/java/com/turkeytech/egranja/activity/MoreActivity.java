package com.turkeytech.egranja.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.adapter.HomeProductAdapter;
import com.turkeytech.egranja.model.Product;
import com.turkeytech.egranja.util.NetworkHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.turkeytech.egranja.session.Constants.PRODUCTS_CATEGORY;
import static com.turkeytech.egranja.session.Constants.PRODUCTS_NODE;

public class MoreActivity extends AppCompatActivity {

    private static final String TAG = "xix: MoreActivity";

    @BindView(R.id.more_appBarLayout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.more_frameLayout)
    FrameLayout mFrameLayout;

    @BindView(R.id.more_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.more_txtToolbar)
    TextView mToolbarText;

    @BindView(R.id.more_recyclerView)
    RecyclerView mRecyclerView;

    private int mSpanCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        ButterKnife.bind(this);

        start();
    }

    private void start() {
        if (NetworkHelper.hasNetwork(this)) {

            mAppBarLayout.setVisibility(View.VISIBLE);
            mFrameLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            findViewById(R.id.more_noData).setVisibility(View.GONE);

            mRecyclerView.hasFixedSize();

            mSpanCount = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? 3 : 2;

            String categoryName = getIntent().getStringExtra(PRODUCTS_CATEGORY);
            mToolbarText.setText(categoryName);
            setProductDataSource(categoryName);
        } else {
            mAppBarLayout.setVisibility(View.GONE);
            mFrameLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            findViewById(R.id.more_noData).setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.retry_button)
    public void retry(){
        start();
    }

    private DatabaseReference getDatabaseReference() {
        return FirebaseDatabase
                .getInstance()
                .getReference()
                .child(PRODUCTS_NODE);
    }

    private void setProductDataSource(String mCategoryName) {
        getDatabaseReference()
                .orderByChild(PRODUCTS_CATEGORY)
                .equalTo(mCategoryName)
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<Product> products = new ArrayList<>();
                                Log.i(TAG, "onDataChange: New Product ArrayList created.");

                                int i = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Product product = snapshot.getValue(Product.class);
                                    assert product != null;
                                    product.setProductId(snapshot.getKey());
                                    if (!products.contains(product)) {

                                        products.add(product);
                                    }
                                    Log.i(TAG, "onDataChange: Product added!");
                                    Log.i(TAG, "onDataChange: Product: "
                                            + products.get(i++));
                                }
                                mRecyclerView.setLayoutManager(new GridLayoutManager(MoreActivity.this, mSpanCount));
                                mRecyclerView.setAdapter(new HomeProductAdapter(MoreActivity.this, products, false));
                                mProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.i(TAG, "onCancelled: " + databaseError.getMessage());
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }
                );

    }

    @OnClick(R.id.more_btnToolbarBack)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
