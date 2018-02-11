package com.turkeytech.egranja.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.turkeytech.egranja.session.Constants.USERS_ANSWER;
import static com.turkeytech.egranja.session.Constants.USERS_NAME;
import static com.turkeytech.egranja.session.Constants.USERS_NODE;
import static com.turkeytech.egranja.session.Constants.USERS_NUMBER;
import static com.turkeytech.egranja.session.Constants.USERS_QUESTION;

public class EditUserActivity extends AppCompatActivity {
    private static final String TAG = "xix: EditUser";

//    public static final String TAG = "xix: EditUserActivity";

    @BindView(R.id.editUser_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.editUser_root)
    CoordinatorLayout rootLayout;

    @BindView(R.id.editUser_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.editUser_txtFirstName)
    TextInputEditText mTextFirstName;

    @BindView(R.id.editUser_txtLastName)
    TextInputEditText mTextLastName;

    @BindView(R.id.editUser_txtNumber)
    TextInputEditText mTextNumber;

    @BindView(R.id.editUser_txtEmail)
    TextInputEditText mTextEmail;

    private String mNewFirstName;
    private String mNewLastName;
    private String mNewNumber;
    private String mNewEmail;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initFirebase();

        if (savedInstanceState == null) {
            fillUiFromDatabase();
        }
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
                mTextFirstName.setText(user.getName().split(" ")[0]);
                mTextLastName.setText(user.getName().split(" ")[1]);
                mTextEmail.setText(mCurrentUser.getEmail());
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

    private void showEditSecurityPopup() {

        // Create a builder for editSecurityPopup
        AlertDialog.Builder editSecurityBuilder = new AlertDialog.Builder(this);


        // Give audioPopup a title of 'Edit Your Security Credentials'
        editSecurityBuilder.setTitle("Edit Your Security Credentials");
        editSecurityBuilder.setMessage("You may update either question or answer or both.");


        // Inflate the view to be used with the editSecurityPopup
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.popup_edit_user_security_question, null);

        // Set the inflated view as the content for
        // editSecurityPopup
        editSecurityBuilder.setView(view);


        // Show audioPopup'savePossible dialog interface

        final AlertDialog editSecurityPopUp = editSecurityBuilder.create();


        // Initialize the views in the inflated layout
        final TextInputEditText textAnswer = view.findViewById(R.id.editUser_txtAnswer);
        final Spinner questions = view.findViewById(R.id.editUser_securityQuestion);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.security_questions,
                android.R.layout.simple_list_item_1
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questions.setAdapter(adapter);

        Button updateSecurity = view.findViewById(R.id.editUser_btnUpdateSecurityCredentials);
        updateSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String answer = textAnswer.getText().toString().trim();

                boolean hasAnswer = !TextUtils.isEmpty(answer);
                boolean hasQuestion = questions.getSelectedItemId() != 0;

                if (hasAnswer || hasQuestion) {

                    Map<String, Object> credentials = new HashMap<>();

                    if (hasQuestion) {
                        credentials.put(USERS_QUESTION, questions.getSelectedItem().toString());
                    }
                    if (hasAnswer) {
                        credentials.put(USERS_ANSWER, answer);
                    }

                    mDatabaseUser.updateChildren(credentials, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                    editSecurityPopUp.dismiss();
                    mProgressBar.setVisibility(View.VISIBLE);
                }

            }
        });

        editSecurityPopUp.show();
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
            case R.id.action_edit_security_question:
                showEditSecurityPopup();
                break;
        }
        return true;
    }

    private void saveDetails() {

        if (savePossible()) {
            mProgressBar.setVisibility(View.VISIBLE);
            mCurrentUser.updateEmail(mNewEmail)
                    .addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        HashMap<String, Object> details = new HashMap<>();
                                        details.put(USERS_NAME, mNewFirstName + " " + mNewLastName);
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
        mNewLastName = mTextLastName.getText().toString().trim();
        mNewNumber = mTextNumber.getText().toString().trim();
        mNewEmail = mTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(mNewFirstName)) {
            mTextFirstName.setError("No First Name Given!");
            return false;
        } else if (mNewFirstName.split(" ").length > 1) {
            mTextFirstName.setError("Enter Your First Name Only!");
            return true;
        } else if (TextUtils.isEmpty(mNewLastName)) {
            mTextLastName.setError("No Last Name Given!");
            return false;
        } else if (mNewLastName.split(" ").length > 1) {
            mTextLastName.setError("Enter Your Last Name Only!");
            return false;
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
}
