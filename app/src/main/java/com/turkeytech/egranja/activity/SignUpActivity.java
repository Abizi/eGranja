package com.turkeytech.egranja.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.model.User;
import com.turkeytech.egranja.util.NetworkHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.turkeytech.egranja.session.Constants.USERS_NODE;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.signUp_root)
    CoordinatorLayout rootLayout;

    @BindView(R.id.signUp_appBarLayout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.signUp_nestedScrollView)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.signUp_txtFirstName)
    TextInputEditText mTextFirstName;

    @BindView(R.id.signUp_txtLastName)
    TextInputEditText mTextLastName;

    @BindView(R.id.signUp_txtNumber)
    TextInputEditText mTextNumber;

    @BindView(R.id.signUp_txtEmail)
    TextInputEditText mTextEmail;

    @BindView(R.id.signUp_txtPassword)
    TextInputEditText mTextPassword;

    @BindView(R.id.signUp_securityQuestion)
    Spinner mSpinSecurityQuestion;

    @BindView(R.id.signUp_txtAnswer)
    TextInputEditText mTextAnswer;

    DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private String mFirstName;
    private String mLastName;
    private String mNumber;
    private String mEmail;
    private String mPassword;
    private String mSecurityQuestion;
    private String mAnswer;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        start();
    }

    private void start() {
        if (NetworkHelper.hasNetwork(this)) {

            mAppBarLayout.setVisibility(View.VISIBLE);
            mNestedScrollView.setVisibility(View.VISIBLE);
            findViewById(R.id.signUp_noData).setVisibility(View.GONE);

            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();


            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.security_questions,
                    android.R.layout.simple_list_item_1
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinSecurityQuestion.setAdapter(adapter);
        } else {
            mAppBarLayout.setVisibility(View.GONE);
            mNestedScrollView.setVisibility(View.GONE);
            findViewById(R.id.signUp_noData).setVisibility(View.VISIBLE);
        }
    }


    @OnClick(R.id.retry_button)
    public void retry(){
        start();
    }

    @OnClick(R.id.signUp_btnSignUp)
    public void signUp() {

        if (allDataSet()) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Creating Account");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                String userId = mAuth.getCurrentUser().getUid();

                                String name = mFirstName + " " + mLastName;

                                writeUser(userId, name, mNumber, mSecurityQuestion, mAnswer);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mProgressDialog.dismiss();

                            if (e instanceof FirebaseAuthUserCollisionException) {
                                showMessage("This Email is already in use!");
                            } else {
                                showMessage(e.getLocalizedMessage());
                            }
                        }
                    });
        }
    }

    private boolean allDataSet() {

        mFirstName = mTextFirstName.getText().toString().trim();
        mLastName = mTextLastName.getText().toString().trim();
        mNumber = mTextNumber.getText().toString().trim();
        mEmail = mTextEmail.getText().toString().trim();
        mPassword = mTextPassword.getText().toString().trim();
        mAnswer = mTextAnswer.getText().toString().trim();
        mSecurityQuestion = mSpinSecurityQuestion.getSelectedItem().toString();

        if (TextUtils.isEmpty(mFirstName)) {

            mTextFirstName.setError("Enter First Name");
            return false;

        } else if (TextUtils.isEmpty(mLastName)) {

            mTextLastName.setError("Enter Last Name");
            return false;

        } else if (TextUtils.isEmpty(mNumber)) {

            mTextNumber.setError("Enter Number");
            return false;

        } else if (TextUtils.isEmpty(mEmail)) {

            mTextEmail.setError("Enter Email Address");
            return false;

        } else if (TextUtils.isEmpty(mPassword)) {

            mTextPassword.setError("Enter Your Password");
            return false;

        } else if (mSpinSecurityQuestion.getSelectedItemPosition() == 0) {

            showMessage("Select Security Question!");
            return false;

        } else if (TextUtils.isEmpty(mAnswer)) {

            mTextAnswer.setError("Enter Your Answer");
            return false;

        } else {
            return true;
        }

    }

    private void writeUser(String userId, String name,
                           String number, String question, String answer) {
        final User user = new User(name, number, question, answer);

        mDatabase.child(USERS_NODE).child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    mProgressDialog.dismiss();

                    Toast.makeText(
                            SignUpActivity.this,
                            "Success: User registered to database",
                            Toast.LENGTH_SHORT
                    ).show();
                    Intent intent = new Intent(SignUpActivity.this, UserDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    showMessage("Error: " + task.getException().getMessage());
                }
            }
        });
    }

    private void showMessage(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
