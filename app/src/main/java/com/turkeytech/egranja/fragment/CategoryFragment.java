package com.turkeytech.egranja.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.adapter.CategoryProductAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.turkeytech.egranja.session.Constants.CATEGORIES_NODE;

public class CategoryFragment extends Fragment {
    private static final String TAG = "xix: CategoryFragment";
    private ArrayList<String> mCategories;
    private HashMap<String, ArrayList<String>> mMap;
    private RecyclerView mRecyclerView;
    private Activity mActivity;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategories = new ArrayList<>();
        mMap = new HashMap<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView: Inflation, inflation, inflation.");
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mActivity = getActivity();

        mRecyclerView = view.findViewById(R.id.categoryFragment_recyclerView);
        mActivity.findViewById(R.id.main_ProgressBar).setVisibility(View.VISIBLE);
        getDataSource();
        Log.i(TAG, "onViewCreated: All thing here are done.");
    }

    private DatabaseReference getDatabaseReference() {
        return FirebaseDatabase
                .getInstance()
                .getReference()
                .child(CATEGORIES_NODE);
    }

    private void getDataSource() {
        getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Map<String, Boolean>> k = (Map) dataSnapshot.getValue();

                for (Map.Entry<String, Map<String, Boolean>> entry : k.entrySet()) {
                    ArrayList<String> product = new ArrayList<>();
                    product.addAll(entry.getValue().keySet());
                    mCategories.add(entry.getKey());
                    mMap.put(entry.getKey(), product);
                    Log.i(TAG, "onDataChange: Key: " + entry.getKey() + "" +
                            " Value: " + entry.getValue().keySet());
                }

                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                Log.i(TAG, "onViewCreated: mCategories: " + mCategories);
                Log.i(TAG, "onViewCreated: mMap: " + mMap);
                mRecyclerView.setAdapter(new CategoryProductAdapter(getContext(), mCategories));

                mActivity.findViewById(R.id.main_ProgressBar).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

}


