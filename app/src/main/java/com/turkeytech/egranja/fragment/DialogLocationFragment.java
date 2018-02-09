package com.turkeytech.egranja.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.turkeytech.egranja.R;

import java.util.HashMap;

public class DialogLocationFragment extends DialogFragment {

    private static final String CONTEXT_KEY = "context_key";
    ListView mListView;
    Button mDismissButton;
    SearchView mSearchView;
    ArrayAdapter<String> mAdapter;
    FragmentDialogLocationListener mHost;
    static HashMap<String, Context> mContextMap = new HashMap<>();

    public DialogLocationFragment(){}

    public static DialogLocationFragment newInstance(Context context) {
        mContextMap.put(CONTEXT_KEY, context);
        return new DialogLocationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_location_list, container, false);


        getDialog().setTitle("Select City");


        mListView = rootView.findViewById(R.id.listView1);
        mSearchView = rootView.findViewById(R.id.searchView1);
        mDismissButton = rootView.findViewById(R.id.dismiss);


        mAdapter = new ArrayAdapter<>(
                mContextMap.get(CONTEXT_KEY),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.cities));

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mHost.onLocationSelected(adapterView.getItemAtPosition(i).toString());

                dismiss();
            }
        });


        mSearchView.setQueryHint("Search Location");

        mSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String txt) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String txt) {
                mAdapter.getFilter().filter(txt);
                return false;
            }
        });


        mDismissButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dismiss();
            }
        });
        return rootView;
    }

    public interface FragmentDialogLocationListener {
        void onLocationSelected(String location);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentDialogLocationListener) {
            mHost = (FragmentDialogLocationListener) context;
        } else {
            throw new ClassCastException("Caller must implement Listener");
        }
    }
}