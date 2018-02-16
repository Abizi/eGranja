package com.turkeytech.egranja.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.activity.HomeActivity;
import com.turkeytech.egranja.adapter.ListProductAdapter;
import com.turkeytech.egranja.model.Product;

import java.util.ArrayList;

import static com.turkeytech.egranja.session.Constants.PRODUCTS_NODE;
import static com.turkeytech.egranja.session.Constants.PRODUCTS_USER_ID;


public class UserProductsFragment extends Fragment {

    private FirebaseUser mFireBaseUser;
    private ArrayList<Product> mProducts;
    private RecyclerView mRecyclerView;
    private Activity mActivity;

    public UserProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProducts = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_products, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().findViewById(R.id.main_ProgressBar).setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mRecyclerView = view.findViewById(R.id.userProductFragment_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActivity = getActivity();
        mActivity.findViewById(R.id.main_fab).setVisibility(View.GONE);
        getDataSource();
    }

    private DatabaseReference getDatabaseReference() {

        return FirebaseDatabase
                .getInstance()
                .getReference()
                .child(PRODUCTS_NODE);
    }

    private void getDataSource() {
        getDatabaseReference()
                .orderByChild(PRODUCTS_USER_ID)
                .equalTo(mFireBaseUser.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            Product product = dataSnapshot.getValue(Product.class);
                            product.setProductId(dataSnapshot.getKey());
                            mProducts.add(product);
                        mRecyclerView.setAdapter(new ListProductAdapter(getContext(), mProducts));
                        mActivity.findViewById(R.id.main_ProgressBar).setVisibility(View.GONE);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                        Intent intent = getActivity().getIntent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        getActivity().finish();
                        startActivity(intent);

                        Toast.makeText(getContext(), "Product Deleted!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mRecyclerView.setAdapter(new ListProductAdapter(getContext(), mProducts));
    }
}
