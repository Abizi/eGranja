package com.turkeytech.egranja.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.turkeytech.egranja.R;

import static com.turkeytech.egranja.session.Constants.FULL_SCREEN_IMAGE;


public class FullScreenImageFragment extends Fragment {


    private String mImageLocation;
    private static Context mContext;

    public FullScreenImageFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FullScreenImageFragment newInstance(Context context, String imageLocation) {
        FullScreenImageFragment fragment = new FullScreenImageFragment();

        mContext = context;

        Bundle args = new Bundle();
        args.putString(FULL_SCREEN_IMAGE, imageLocation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageLocation = getArguments().getString(FULL_SCREEN_IMAGE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);
        ImageView imageView = view.findViewById(R.id.fullScreenImage_image);
        Glide.with(mContext).load(mImageLocation).into(imageView);
        return view;
    }

}
