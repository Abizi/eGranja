package com.turkeytech.egranja.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.fragment.CategoryFragment;
import com.turkeytech.egranja.fragment.HomeFragment;
import com.turkeytech.egranja.fragment.UserProductsFragment;
import com.turkeytech.egranja.model.User;
import com.turkeytech.egranja.util.NetworkHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.turkeytech.egranja.session.Constants.ACTIVITY_HOME;
import static com.turkeytech.egranja.session.Constants.ACTIVITY_LOGIN;
import static com.turkeytech.egranja.session.Constants.ACTIVITY_UPLOAD;
import static com.turkeytech.egranja.session.Constants.ACTIVITY_USER;
import static com.turkeytech.egranja.session.Constants.ANONYMOUS;
import static com.turkeytech.egranja.session.Constants.HOME_SCREEN;
import static com.turkeytech.egranja.session.Constants.LOGIN_SENDER;
import static com.turkeytech.egranja.session.Constants.NAV_CATEGORY;
import static com.turkeytech.egranja.session.Constants.NAV_HOME;
import static com.turkeytech.egranja.session.Constants.NAV_PRODUCTS;
import static com.turkeytech.egranja.session.Constants.USERS_NODE;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //    private static final String TAG = "xix: HomeActivity";
    @BindView(R.id.main_rootLayout)
    CoordinatorLayout rootLayout;

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.main_fab)
    FloatingActionButton mFab;

    @BindView(R.id.main_ProgressBar)
    ProgressBar mProgressBar;

    // Firebase Instance Variables
    FirebaseUser mFirebaseUser;

    // Fragment Trackers
    private Fragment mCurrentFragment;
    private FragmentManager mFragmentManager;

    // User Details
    private String mPhotoUrl;

    // Redirecting Login trouble
    private boolean isHome;
    private Menu mNavMenu;
    private TextView mFullName;
    private TextView mEmail;
    private ImageView mImage;

    // Google

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        // Replace action bar and set custom mToolbar as actionbar
        setSupportActionBar(mToolbar);


        // Setting up the Up button
        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        initDrawerItems();

        setScreen(getIntent().getStringExtra(HOME_SCREEN));

        // Initialize Firebase Auth
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null) {

            mNavigationView.inflateMenu(R.menu.activity_main_drawer_in);

            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(USERS_NODE)
                    .child(mFirebaseUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            mFullName.setText(user.getName());
                            mEmail.setText(mFirebaseUser.getEmail());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            if (mFirebaseUser.getPhotoUrl() != null) {
                Glide.with(this).load(mFirebaseUser.getPhotoUrl().toString()).into(mImage);
            }

        } else {
            mFullName.setText(ANONYMOUS);
            mNavigationView.inflateMenu(R.menu.activity_main_drawer_out);
        }

        NetworkHelper.hasNetwork(this);
    }


    private void initDrawerItems() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        View navHeader = mNavigationView.getHeaderView(0);
        mFullName = navHeader.findViewById(R.id.navHeader_name);
        mEmail = navHeader.findViewById(R.id.navHeader_email);
        mImage = navHeader.findViewById(R.id.navHeader_image);

        mFragmentManager = getSupportFragmentManager();
    }

    private void setScreen(String screen) {
        if (screen != null) {
            setDisplayFragment(screen);
        } else {
            setDisplayFragment(NAV_HOME);
        }
    }

    private void setDisplayFragment(String TAG) {
        switch (TAG) {
            case NAV_HOME:
                HomeFragment homeFragment = new HomeFragment();
                mFragmentManager
                        .beginTransaction()
                        .replace(R.id.contentHome_fragment, homeFragment, TAG)
                        .commit();

                mToolbar.setTitle(NAV_HOME);
                mCurrentFragment = homeFragment;
                break;

            case NAV_CATEGORY:
                CategoryFragment categoryFragment = new CategoryFragment();
                mFragmentManager
                        .beginTransaction()
                        .replace(R.id.contentHome_fragment, categoryFragment, TAG)
                        .commit();

                mToolbar.setTitle(NAV_CATEGORY);
                mCurrentFragment = categoryFragment;
                break;

            case NAV_PRODUCTS:
                UserProductsFragment userProductsFragment = new UserProductsFragment();
                mFragmentManager
                        .beginTransaction()
                        .replace(R.id.contentHome_fragment, userProductsFragment, TAG)
                        .commit();

                mToolbar.setTitle(NAV_PRODUCTS);
                mCurrentFragment = userProductsFragment;
                break;
        }
    }

    private void setDisplayActivity(String activity) {
        switch (activity) {
            case ACTIVITY_USER:
                startActivity(new Intent(this, UserDetailActivity.class));
                break;
            case ACTIVITY_HOME:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case ACTIVITY_UPLOAD:
                startActivity(new Intent(this, AddProductActivity.class));
                break;
            case ACTIVITY_LOGIN:
                Intent intent = new Intent(this, LoginActivity.class);

                // This trouble is for redirecting from Logging in
                if (isHome) {
                    intent.putExtra(LOGIN_SENDER, ACTIVITY_HOME);
                } else {
                    intent.putExtra(LOGIN_SENDER, ACTIVITY_UPLOAD);
                }
                startActivity(intent);
                finish();
                break;
        }
    }


    @OnClick(R.id.main_fab)
    public void uploadProduct() {
        if (mFirebaseUser != null) {
            setDisplayActivity(ACTIVITY_UPLOAD);
        } else {
            Snackbar.make(
                    rootLayout,
                    "Please Sign In To Perform This Action",
                    Snackbar.LENGTH_LONG
            ).setAction("Sign In", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDisplayActivity(ACTIVITY_LOGIN);
                    isHome = false;
                }
            }).show();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            setDisplayFragment(NAV_HOME);

        } else if (id == R.id.nav_category) {

            setDisplayFragment(NAV_CATEGORY);

        } else if (id == R.id.nav_products) {

            setDisplayFragment(NAV_PRODUCTS);

        } else if (id == R.id.nav_udetails) {

            setDisplayActivity(ACTIVITY_USER);

        } else if (id == R.id.nav_add_product) {

            setDisplayActivity(ACTIVITY_UPLOAD);

        } else if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_login) {
            logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void logout() {
        if (mFirebaseUser != null) {
            mFirebaseUser = null;
            FirebaseAuth.getInstance().signOut();
            mFullName.setText(ANONYMOUS);
            setDisplayActivity(ACTIVITY_HOME);
        } else {
            setDisplayActivity(ACTIVITY_LOGIN);
            isHome = true;
        }
    }


    @Override
    public void onBackPressed() {
        mDrawer = findViewById(R.id.drawer_layout);
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (mFragmentManager.findFragmentByTag("Home") == mCurrentFragment) {

                this.finishAffinity();

            } else {
                setScreen(NAV_HOME);
            }
        }
    }
}
