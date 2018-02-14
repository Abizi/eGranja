package com.turkeytech.egranja.activity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.model.User;
import com.turkeytech.egranja.util.NetworkHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.turkeytech.egranja.session.Constants.USERS_NODE;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final int MY_NOTIFICATION = 901;

    @BindView(R.id.forgotPassword_nestedScrollView)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.forgotPassword_appBarLayout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.forgotPassword_root)
    CoordinatorLayout mRootLayout;

    @BindView(R.id.forgotPassword_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.forgotPassword_btnToolbarClose)
    ImageView mBackButton;

    @BindView(R.id.forgotPassword_txtEmail)
    TextInputEditText mTextEmail;

    @BindView(R.id.forgotPassword_securityQuestion)
    Spinner mSecurityQuestions;

    @BindView(R.id.forgotPassword_txtAnswer)
    TextInputEditText mTextAnswer;

    private String mEmail;
    private String mAnswer;
    private String mSecurityQuestion;

    private FirebaseUser mCurrentUser;

    private NotificationManager mNotifyMgr;
    private NotificationCompat.Builder mBuilderSuccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);

        start();
    }

    private void start() {
        if (NetworkHelper.hasNetwork(this)) {

            mAppBarLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mNestedScrollView.setVisibility(View.VISIBLE);
            findViewById(R.id.forgotPassword_noData).setVisibility(View.GONE);


            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

            mBuilderSuccess = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_upload)
                    .setContentText("Updating Password");

            mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        } else {
            mAppBarLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mNestedScrollView.setVisibility(View.GONE);
            findViewById(R.id.forgotPassword_noData).setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.retry_button)
    public void retry(){
        start();
    }


    private DatabaseReference getUserReference() {
        return FirebaseDatabase
                .getInstance()
                .getReference()
                .child(USERS_NODE)
                .child(mCurrentUser.getUid());
    }

    @OnClick(R.id.forgotPassword_btnVerify)
    public void onVerifyClick(){
        if (allDataSet()) {
            mProgressBar.setVisibility(View.VISIBLE);

            getUserReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    mProgressBar.setVisibility(View.GONE);
                    if (verifyOk(user)){
                        showEditPasswordPopup();
                    } else {
                        showMessage("Invalid Credentials");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mProgressBar.setVisibility(View.GONE);
                    showMessage(databaseError.getMessage());
                }
            });
        }
    }

    private boolean allDataSet(){
        if (false) {
            return true;
        } else {
            return false;
        }
    }

    private boolean verifyOk(User user){
        return user.getQuestion().equals(mSecurityQuestion)
                && user.getAnswer().equals(mAnswer);
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
                            .addOnCompleteListener(ForgotPasswordActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mBuilderSuccess.setContentText("Password Updated Successfully!");
                                    mBuilderSuccess.setProgress(0, 0, false);
                                    mNotifyMgr.notify(MY_NOTIFICATION, mBuilderSuccess.build());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mBuilderSuccess.setContentText("Password Update Failed!");
                                    mBuilderSuccess.setProgress(0, 0, false);
                                    mNotifyMgr.notify(MY_NOTIFICATION, mBuilderSuccess.build());
                                }
                            });
                    editPasswordPopUp.dismiss();
                    mProgressBar.setVisibility(View.VISIBLE);
                }

            }
        });

        editPasswordPopUp.show();
    }

    // Display a snackbar message to the user
    private void showMessage(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
