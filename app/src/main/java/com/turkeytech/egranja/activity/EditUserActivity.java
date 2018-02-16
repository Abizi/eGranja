package com.turkeytech.egranja.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.model.User;
import com.turkeytech.egranja.util.NetworkHelper;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.turkeytech.egranja.session.Constants.USERS_NAME;
import static com.turkeytech.egranja.session.Constants.USERS_NODE;
import static com.turkeytech.egranja.session.Constants.USERS_NUMBER;

public class EditUserActivity extends AppCompatActivity {
    private static final String TAG = "xix: EditUser";

//    public static final String TAG = "xix: EditUserActivity";

    @BindView(R.id.editUser_appBarLayout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.editUser_nestedScrollView)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.editUser_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.editUser_root)
    CoordinatorLayout rootLayout;

    @BindView(R.id.editUser_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.editUser_txtFirstName)
    TextInputEditText mTextFirstName;

    @BindView(R.id.editUser_txtNumber)
    TextInputEditText mTextNumber;

    @BindView(R.id.editUser_txtEmail)
    TextInputEditText mTextEmail;

    private String mNewFirstName;
    private String mNewNumber;
    private String mNewEmail;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;
    private Bundle mSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        ButterKnife.bind(this);

        mSavedInstanceState = savedInstanceState;
        start(mSavedInstanceState);
    }

    private void start(Bundle savedInstanceState) {
        if (NetworkHelper.hasNetwork(this)) {

            mAppBarLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mNestedScrollView.setVisibility(View.VISIBLE);
            findViewById(R.id.editUser_noData).setVisibility(View.GONE);

            setSupportActionBar(mToolbar);
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            initFirebase();

            if (savedInstanceState == null) {
                fillUiFromDatabase();
            }
        } else {
            mAppBarLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mNestedScrollView.setVisibility(View.GONE);
            findViewById(R.id.editUser_noData).setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.retry_button)
    public void retry() {
        start(mSavedInstanceState);
    }


    private void initFirebase() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseUser = mDatabase.child(USERS_NODE).child(mCurrentUser.getUid());
    }

    public void fillUiFromDatabase() {

        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                mTextFirstName.setText(mCurrentUser.getDisplayName());
                mTextEmail.setText(mCurrentUser.getEmail());
                assert user !=  null;
                mTextNumber.setText(user.getNumber());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                showMessage(databaseError.getMessage());
                Log.e(TAG, "onCancelled: " + databaseError.getDetails(), databaseError.toException());

            }
        });
    }


    private void showEditPasswordPopup() {

        // Create a builder for editPasswordPopup
        AlertDialog.Builder editPasswordBuilder = new AlertDialog.Builder(this);


        // Give audioPopup a title of 'Change Your Password'
        editPasswordBuilder.setTitle("Change Your Password");


        // Inflate the view to be used with the editPasswordPopup dialog
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.popup_edit_user_password, null);

        // Set the inflated view as the content for
        // editPasswordPopup
        editPasswordBuilder.setView(view);


        final AlertDialog editPasswordPopUp = editPasswordBuilder.create();


        // Initialize the views in the inflated layout
        final TextInputEditText textPassword = view.findViewById(R.id.editUser_txtPassword);

        Button updatePassword = view.findViewById(R.id.editUser_btnUpdatePassword);

        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newPassword = textPassword.getText().toString();

                if (!TextUtils.isEmpty(newPassword)) {
                    mCurrentUser.updatePassword(newPassword)
                            .addOnCompleteListener(EditUserActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mProgressBar.setVisibility(View.GONE);
                                    showMessage("Password Updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mProgressBar.setVisibility(View.GONE);
                                    showMessage(e.getLocalizedMessage());
                                }
                            });
                    editPasswordPopUp.dismiss();
                    mProgressBar.setVisibility(View.VISIBLE);
                }

            }
        });

        editPasswordPopUp.show();
    }


    private void showMessage(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save_edit_user:
                saveDetails();
                break;
            case R.id.action_edit_password:
                showEditPasswordPopup();
                break;
        }
        return true;
    }

    private void saveDetails() {

        if (savePossible()) {
            mProgressBar.setVisibility(View.VISIBLE);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(mNewFirstName)
                    .build();
            mCurrentUser.updateProfile(profileUpdates);
            mCurrentUser.updateEmail(mNewEmail)
                    .addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        HashMap<String, Object> details = new HashMap<>();
                                        details.put(USERS_NUMBER, mNewNumber);

                                        mDatabaseUser.updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mProgressBar.setVisibility(View.GONE);
                                                startActivity(new Intent(EditUserActivity.this, UserDetailActivity.class));
                                                finish();
                                            }
                                        });

                                    }
                                }
                            }
                    )
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage(e.getLocalizedMessage());
                        }
                    });

        }
    }

    private boolean savePossible() {

        mNewFirstName = mTextFirstName.getText().toString().trim();
        mNewNumber = mTextNumber.getText().toString().trim();
        mNewEmail = mTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(mNewFirstName)) {
            mTextFirstName.setError("No First Name Given!");
            return false;
        } else if (mNewFirstName.split(" ").length > 1) {
            mTextFirstName.setError("Enter Your First Name Only!");
            return true;
        } else if (TextUtils.isEmpty(mNewNumber)) {
            mTextNumber.setError("No Number Given!");
            return false;
        } else if (mNewNumber.length() < 9) {
            mTextNumber.setError("Invalid Number!");
            return false;
        } else if (TextUtils.isEmpty(mNewEmail)) {
            mTextEmail.setError("No Email Given!");
            return false;
        } else if (!mNewEmail.contains("@")) {
            mTextEmail.setError("Invalid Email!");
            return false;
        } else {
            return true;
        }

    }

    @OnClick(R.id.editUser_btnToolbarClose)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
