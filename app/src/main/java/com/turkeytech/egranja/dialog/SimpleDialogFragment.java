package com.turkeytech.egranja.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

//TO use this dialog fragment instantiate this class
// and call the show method.
// Also, add the interface to the calling class
// and override the interface methods.


public class SimpleDialogFragment extends DialogFragment {

    public static final String TAG = "SimpleDialogFragment";

    private static final String TITLE_KEY = "title_key";
    private static final String MESSAGE_KEY = "message_key";
    public static final String POSITIVE_BUTTON_KEY = "positive_button_key";
    public static final String NEGATIVE_BUTTON_KEY = "negative_button_key";
    public static final String NEUTRAL_BUTTON_KEY = "neutral_button_key";

    private SimpleDialogListener mHost;
    private String message;
    private String title;
    private String positiveText;
    private String negativeText;
    private String neutralText;
    private boolean hasNeutralButton;


    public static SimpleDialogFragment newInstance(
            String title, String message, String positiveButton, String negativeButton) {

        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putString(MESSAGE_KEY, message);
        args.putString(POSITIVE_BUTTON_KEY, positiveButton);
        args.putString(NEGATIVE_BUTTON_KEY, negativeButton);

        SimpleDialogFragment fragment = new SimpleDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SimpleDialogFragment newInstance(
            String title, String message, String positiveButton, String negativeButton, String neutralButton) {

        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putString(MESSAGE_KEY, message);
        args.putString(POSITIVE_BUTTON_KEY, positiveButton);
        args.putString(NEGATIVE_BUTTON_KEY, negativeButton);
        args.putString(NEUTRAL_BUTTON_KEY, neutralButton);

        SimpleDialogFragment fragment = new SimpleDialogFragment();
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
            message = bundle.getString(MESSAGE_KEY);
            positiveText = bundle.getString(POSITIVE_BUTTON_KEY);
            negativeText = bundle.getString(NEGATIVE_BUTTON_KEY);

            if (bundle.getString(NEUTRAL_BUTTON_KEY) != null) {
                hasNeutralButton = true;
                neutralText = bundle.getString(NEUTRAL_BUTTON_KEY);
            }else {
                hasNeutralButton = false;
            }

        }

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "onClick: Positive Button Clicked!");
                mHost.onPositiveResult(SimpleDialogFragment.this);
            }
        });
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "onClick: Negative Button Button Clicked!");
                mHost.onNegativeResult(SimpleDialogFragment.this);
            }
        });

        if (hasNeutralButton) {
            builder.setNeutralButton(neutralText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.i(TAG, "onClick: Neutral Button Clicked!");
                    mHost.onNeutralResult(SimpleDialogFragment.this);
                }
            });
        }

        return builder.create();
    }

    public interface SimpleDialogListener {

        void onPositiveResult(DialogFragment dialogFragment);

        void onNegativeResult(DialogFragment dialogFragment);

        void onNeutralResult(DialogFragment dialogFragment);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.i(TAG, "onCancel: User cancelled the dialog!");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SimpleDialogListener) {
            mHost = (SimpleDialogListener) context;
        } else {
            throw new ClassCastException("Caller must implement Listener");
        }
    }
}
