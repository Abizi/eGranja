package com.turkeytech.egranja.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.util.NetworkHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.turkeytech.egranja.session.Constants.ACTIVITY_HOME;
import static com.turkeytech.egranja.session.Constants.ACTIVITY_UPLOAD;
import static com.turkeytech.egranja.session.Constants.LOGIN_SENDER;


public class LoginActivity extends AppCompatActivity {

//    private static final String TAG = "SignInActivity";

    @BindView(R.id.login_appBarLayout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.login_nestedScrollView)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.login_rootLayout)
    CoordinatorLayout rootLayout;

    @BindView(R.id.login_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.login_txtEmail)
    TextInputEditText mEmailText;

    @BindView(R.id.login_txtPassword)
    TextInputEditText mPasswordText;

    @BindView(R.id.login_btnSignUp)
    Button mSignUpButton;

    @BindView(R.id.login_btnSignIn)
    Button mSignInButton;

    @BindView(R.id.login_btnForgotPassword)
    Button mForgotPasswordButton;

    private String sender;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ButterKnife.bind(this);

        start();
    }

    private void start() {
        if (NetworkHelper.hasNetwork(this)) {

            mNestedScrollView.setVisibility(View.VISIBLE);
            mAppBarLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            findViewById(R.id.login_noDaata).setVisibility(View.GONE);


            sender = getIntent().getStringExtra(LOGIN_SENDER);

            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if (firebaseAuth.getCurrentUser() != null) {
                        backToSender();
                    }
                }
            };
        } else {
            mAppBarLayout.setVisibility(View.GONE);
            mNestedScrollView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            findViewById(R.id.login_noDaata).setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.retry_button)
    public void retry(){
        start();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (null != mAuthListener) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @OnClick(R.id.login_btnSignIn)
    public void signIn() {

        if (TextUtils.isEmpty(mEmailText.getText())) {
            mEmailText.setError("Enter Email Address!");
        } else if (TextUtils.isEmpty(mPasswordText.getText())) {
            mPasswordText.setError("Enter Password!");
        } else {
            signInEmailPassword(
                    mEmailText.getText().toString().trim(),
                    mPasswordText.getText().toString().trim()
            );
        }
    }

    private void signInEmailPassword(String email, String password) {
        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            backToSender();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressBar.setVisibility(View.GONE);
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            showMessage("Invalid Password");
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            showMessage("No Account With This Email");
                        } else {
                            showMessage(e.getLocalizedMessage());
                        }
                    }
                });
    }

    private void backToSender() {
        mProgressBar.setVisibility(View.GONE);
        switch (sender) {
            case ACTIVITY_HOME:
                startNewActivity(new HomeActivity());
                break;
            case ACTIVITY_UPLOAD:
                startNewActivity(new AddProductActivity());
                break;
        }
    }

    private void startNewActivity(Activity activity) {
        Intent intent = new Intent(this, activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.login_btnSignUp)
    public void signUp() {

        startNewActivity(new SignUpActivity());
    }


    private void showMessage(@NonNull String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }
}


