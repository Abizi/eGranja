package com.turkeytech.egranja.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.turkeytech.egranja.R;
import com.turkeytech.egranja.fragment.FullScreenImageFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.turkeytech.egranja.session.Constants.FULL_SCREEN_IMAGE_LIST;

public class FullScreenImageActivity extends AppCompatActivity {

    @BindView(R.id.fullScreenImage_viewPager)
    ViewPager mPager;


    private ArrayList<String> mProductImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_sceren_image);

        ButterKnife.bind(this);

        mProductImages = new ArrayList<>();
        mProductImages = getIntent().getStringArrayListExtra(FULL_SCREEN_IMAGE_LIST);

        PagerAdapter mPagerAdapter = new ImagePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

    }


    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

        ImagePagerAdapter(FragmentManager fm) {

            super(fm);
            fragmentArrayList.add(FullScreenImageFragment.newInstance(FullScreenImageActivity.this, mProductImages.get(0)));
            fragmentArrayList.add(FullScreenImageFragment.newInstance(FullScreenImageActivity.this, mProductImages.get(1)));
            fragmentArrayList.add(FullScreenImageFragment.newInstance(FullScreenImageActivity.this, mProductImages.get(2)));
            fragmentArrayList.add(FullScreenImageFragment.newInstance(FullScreenImageActivity.this, mProductImages.get(3)));

        }

        @Override
        public Fragment getItem(int position) {

            return fragmentArrayList.get(position);

        }


        @Override
        public int getCount() {

            return fragmentArrayList.size();

        }
    }

    @OnClick(R.id.fullScreenImage_btnToolbarBack)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

