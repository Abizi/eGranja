package com.turkeytech.egranja.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.activity.HomeActivity;
import com.turkeytech.egranja.adapter.HomeProductAdapter;
import com.turkeytech.egranja.model.Product;

import java.util.ArrayList;
import java.util.List;

import static com.turkeytech.egranja.session.Constants.PRODUCTS_NAME;
import static com.turkeytech.egranja.session.Constants.PRODUCTS_NODE;
import static com.turkeytech.egranja.session.Constants.PRODUCTS_PRICE;
import static com.turkeytech.egranja.session.Constants.PRODUCTS_TIME_STAMP;
import static com.turkeytech.egranja.session.eGranja.HomeViewType;
import static com.turkeytech.egranja.session.eGranja.SortOrder;

public class HomeFragment extends Fragment {


    private static final String TAG = "xix: Fragment Home";

    private RecyclerView mRecyclerView;
    private ArrayList<Product> mProducts;
    private HomeProductAdapter mAdapter;
    private int mSpanCount;
    private Activity mActivity;

    public HomeFragment() {
        // Required empty public constructor
        Log.i(TAG, "HomeFragment: is called");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: Next !");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.main_ProgressBar).setVisibility(View.VISIBLE);
        mProducts = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.homeFragment_recyclerView);
        mRecyclerView.hasFixedSize();


        mActivity = getActivity();

        mSpanCount = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? 3 : 2;

        Log.i(TAG, "onViewCreated: This  hit");

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
                .orderByChild(SortOrder)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot :
                                dataSnapshot.getChildren()) {
                            Product product = snapshot.getValue(Product.class);
                            product.setProductId(snapshot.getKey());
                            mProducts.add(product);
                        }

                        if (HomeViewType) {
                            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mSpanCount));
                            mAdapter = new HomeProductAdapter(getContext(), mProducts, false);
                        } else {
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            mAdapter = new HomeProductAdapter(getContext(), mProducts, true);
                        }
                        mRecyclerView.setAdapter(mAdapter);

                        mActivity.findViewById(R.id.main_ProgressBar).setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().findViewById(R.id.main_ProgressBar).setVisibility(View.VISIBLE);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);


        final MenuItem searchItem = menu.findItem(R.id.action_home_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.length() > 0) {

                    final List<Product> filteredModelList = mAdapter.filter(mProducts, query);
                    mAdapter.animateTo(filteredModelList);
                    mRecyclerView.scrollToPosition(0);

                } else {

                    mAdapter.animateTo(mProducts);
                    mRecyclerView.scrollToPosition(0);

                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0) {

                    final List<Product> filteredModelList = mAdapter.filter(mProducts, newText);
                    mAdapter.animateTo(filteredModelList);
                    mRecyclerView.scrollToPosition(0);

                } else {

                    mAdapter.animateTo(mProducts);
                    mRecyclerView.scrollToPosition(0);

                }

                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return false;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (item.getItemId()) {

            case R.id.action_home_listToggle:
                startActivity(intent);
                HomeViewType = false;
                return true;

            case R.id.action_home_gridToggle:
                startActivity(intent);
                HomeViewType = true;
                return true;

            case R.id.action_sort_name:
                SortOrder = PRODUCTS_NAME;
                startActivity(intent);
                return true;

            case R.id.action_sort_price:
                SortOrder = PRODUCTS_PRICE;
                startActivity(intent);
                return true;

            case R.id.action_sort_date:
                SortOrder = PRODUCTS_TIME_STAMP;
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}

