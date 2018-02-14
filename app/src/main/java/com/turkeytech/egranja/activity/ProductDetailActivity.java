package com.turkeytech.egranja.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.adapter.ImageAdapter;
import com.turkeytech.egranja.model.Product;
import com.turkeytech.egranja.util.NetworkHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.turkeytech.egranja.session.Constants.PRODUCTS_NODE;
import static com.turkeytech.egranja.session.Constants.PRODUCT_ID;

public class ProductDetailActivity extends AppCompatActivity {

    @BindView(R.id.productDetail_appBarLayout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.productDetail_nestedScrollView)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.productDetail_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.productDetail_rootLayout)
    CoordinatorLayout mRootLayout;

    @BindView(R.id.productDetail_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.productDetail_imgProductImage)
    ImageView mProductImage;

    @BindView(R.id.productDetail_txtProductName)
    TextView mProductName;

    @BindView(R.id.productDetail_txtCategory)
    TextView mProductCategory;

    @BindView(R.id.productDetail_txtFarmName)
    TextView mFarmName;

    @BindView(R.id.productDetail_txtPrice)
    TextView mProductPrice;

    @BindView(R.id.productDetail_txtQuantity)
    TextView mProductQuantity;

    @BindView(R.id.productDetail_txtDate)
    TextView mProductDate;

    @BindView(R.id.productDetail_txtCall)
    TextView mProductNumber;

    @BindView(R.id.productDetail_txtLocation)
    TextView mProductLocation;

    @BindView(R.id.productDetail_txtDescriptionText)
    TextView mProductDescription;

    @BindView(R.id.productDetail_recyclerViewForImages)
    RecyclerView mRecyclerViewImages;

    @BindView(R.id.productDetail_bottomSheet)
    View mBottomSheet;

    private ArrayList<String> images;
    private Product mProduct;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ButterKnife.bind(this);

        start();
    }

    private void start() {
        if (NetworkHelper.hasNetwork(this)) {

            mAppBarLayout.setVisibility(View.VISIBLE);
            mNestedScrollView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            findViewById(R.id.productDetail_noData).setVisibility(View.GONE);

            mRecyclerViewImages.setLayoutManager(
                            new LinearLayoutManager(
                                    this,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                            )
            );
            images = new ArrayList<>();
            bottomSheetHack();
            updateUi(getIntent().getStringExtra(PRODUCT_ID));
        } else {
            mAppBarLayout.setVisibility(View.GONE);
            mNestedScrollView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            findViewById(R.id.productDetail_bottomSheet).setVisibility(View.GONE);
            findViewById(R.id.productDetail_noData).setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.retry_button)
    public void retry(){
        start();
    }

    private DatabaseReference getDatabase() {
        return FirebaseDatabase
                .getInstance()
                .getReference()
                .child(PRODUCTS_NODE);
    }

    private void updateUi(final String productId) {

        getDatabase().child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mProduct = dataSnapshot.getValue(Product.class);

                        Glide.with(ProductDetailActivity.this).load(mProduct.getImage1()).into(mProductImage);
                        mProductName.setText(mProduct.getName());
                        mProductCategory.setText(mProduct.getCategory());
                        mFarmName.setText(mProduct.getUsername());
                        String price = "GhC " + mProduct.getPrice();
                        mProductPrice.setText(price);
                        mProductQuantity.setText(mProduct.getQuantity());
                        mProductDate.setText(mProduct.getTimeStamp());
                        mProductNumber.setText(mProduct.getUsernum());
                        mProductLocation.setText(mProduct.getLocation());
                        mProductDescription.setText(mProduct.getDescription());

                        images.add(mProduct.getImage1());
                        images.add(mProduct.getImage2());
                        images.add(mProduct.getImage3());
                        images.add(mProduct.getImage4());

                        mRecyclerViewImages.setAdapter(new ImageAdapter(ProductDetailActivity.this, images));

                        if (mProduct.getAudio() == null) {

                            ViewGroup lay = findViewById(R.id.productDetail_layAudioDescription);
                            lay.setVisibility(View.GONE);

                            View divider = findViewById(R.id.productDetail_dividerBottomSheet);
                            divider.setVisibility(View.GONE);

                        }
                        if (mProduct.getVideo() == null) {

                            ViewGroup lay = findViewById(R.id.productDetail_layVideoDescription);
                            lay.setVisibility(View.GONE);

                        }
                        if (mProduct.getVideo() == null && mProduct.getAudio() == null) {

                            Button button = findViewById(R.id.productDetail_btnMoreDescription);
                            button.setVisibility(View.GONE);

                        }

                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @OnClick(R.id.productDetail_btnMoreDescription)
    public void onDescButtonClick(View view) {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void bottomSheetHack() {

        mBottomSheet.post(new Runnable() {

            @Override
            public void run() {
                mBottomSheetBehavior.setPeekHeight(0);
            }

        });

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);

    }

    @OnClick(R.id.productDetail_btnViewOnMap)
    public void onMapButtonClick(View view) {

        String bn = "geo:0,0?q=";

        Uri gmmIntentUri = Uri.parse(bn + mProduct.getLocation());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {

            startActivity(mapIntent);

        } else {

            Snackbar.make(
                    mRootLayout,
                    "You Do Have Google Maps Installed!",
                    Snackbar.LENGTH_LONG
            ).show();

        }

    }


    @OnClick(R.id.productDetail_btnCall)
    public void onCallButtonClick() {

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mProduct.getUsernum(), null));

        if (intent.resolveActivity(getPackageManager()) != null) {

            startActivity(intent);

        }

    }

    @OnClick(R.id.productDetail_btnMessage)
    public void onMessageButtonClick() {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mProduct.getUsernum(), null));

        if (intent.resolveActivity(getPackageManager()) != null) {

            startActivity(intent);

        }

    }

    @OnClick(R.id.productDetail_btnAudioDescription)
    public void onPlayAudioClick() {

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(mProduct.getAudio()), "audio/*");
        startActivity(intent);

    }

    @OnClick(R.id.productDetail_btnVideoDescription)
    public void onPlayVideoClick() {

        Intent myIntent = new Intent(this, VideoViewActivity.class);
        myIntent.putExtra("vUrl", mProduct.getVideo());
        this.startActivity(myIntent);

    }

    @OnClick(R.id.productDetail_btnToolbarBack)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
