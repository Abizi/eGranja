package com.turkeytech.egranja.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

//TO use this dialog fragment instantiate this class
// and call the show method.
// Also, add the interface to the calling class
// and override the interface methods.


public class CustomDialogFragment extends DialogFragment {

    public static final String TAG = "SimpleDialogFragment";
    private static final String TITLE_KEY = "title_key";
    private static final String LAYOUT_KEY = "layout_key";

    private CustomDialogListener mHost;
    private String title;
    private int mLayout;


    public static CustomDialogFragment newInstance(String title, @LayoutRes int layout) {

        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putInt(LAYOUT_KEY, layout);

        CustomDialogFragment fragment = new CustomDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(TITLE_KEY);
            mLayout = bundle.getInt(LAYOUT_KEY);
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(mLayout, null);

        builder.setTitle(title)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "onClick: Positive Button Clicked!");
                        mHost.onPositiveResult(CustomDialogFragment.this);
                    }
                })
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "onClick: Negative Button Button Clicked!");
                        mHost.onNegativeResult(CustomDialogFragment.this);
                    }
                })
                .setView(view);

        return builder.create();
    }

    public interface CustomDialogListener {
        void onPositiveResult(DialogFragment dialogFragment);

        void onNegativeResult(DialogFragment dialogFragment);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.i(TAG, "onCancel: User cancelled the dialog!");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CustomDialogListener) {
            mHost = (CustomDialogListener) context;
        } else {
            throw new ClassCastException("Caller must implement Listener");
        }
    }
}
