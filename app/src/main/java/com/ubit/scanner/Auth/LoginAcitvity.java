package com.ubit.scanner.Auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ubit.scanner.Order.OrderActivity;
import com.ubit.scanner.R;
import com.ubit.scanner.model.User;
import com.ubit.scanner.scanner.ScannerActivity;

import java.nio.ByteOrder;
import java.util.Objects;
import java.util.Scanner;

public class LoginAcitvity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private ChildEventListener mChildEventListener;

    private String mUserID;
    private String mType="";

    private TextView mSignUpTextview;
    private Button mLoginButton;
    private EditText mEmailEditText,mPasswordEditText;
    private RadioButton mUserRadioButton,mAdminRadioButton;
    String email,password;
    ProgressBar mprogressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mprogressBar = (ProgressBar) findViewById(R.id.circular_progress_bar);
        mSignUpTextview =(TextView) findViewById(R.id.textViewSignup);
        mEmailEditText = (EditText) findViewById(R.id.editTextEmail);
        mPasswordEditText = (EditText) findViewById(R.id.editTextPassword);
        mLoginButton = (Button) findViewById(R.id.buttonLogin);
        mSignUpTextview.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);

        mUserRadioButton = (RadioButton) findViewById(R.id.user_radio_button);
        mAdminRadioButton = (RadioButton) findViewById(R.id.admin_radio_button);
        mUserRadioButton.setOnClickListener(this);
        mAdminRadioButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.textViewSignup:
                Intent signuUpIntent = new Intent(LoginAcitvity.this, Sign_up.class);
                startActivity(signuUpIntent);
                finish();
                break;

            case R.id.buttonLogin:
                if(!mType.equals("")){
                    Login();
                }
                else{
                    Toast.makeText(this, "check ", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.user_radio_button:
                mType = getResources().getString(R.string.user);
                break;

            case R.id.admin_radio_button:
                mType = getResources().getString(R.string.admin);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {

        if (mChildEventListener != null) {
            mUserDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void Login() {
         email = mEmailEditText.getText().toString().trim();
         password = mPasswordEditText.getText().toString().trim();

        if (email.isEmpty()){

            mEmailEditText.setError("Enter your Email!");
            mEmailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            mEmailEditText.setError("Invalid Email!");
            mEmailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()){
            mPasswordEditText.setError("Enter your Password!");
            mPasswordEditText.requestFocus();
            return;
        }

        if (password.length()<6){
            mPasswordEditText.setError("Minimum Password Limit is 6");
            mPasswordEditText.requestFocus();
            return;
        }
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mprogressBar.setVisibility(View.INVISIBLE);
                            if (task.isSuccessful()){
                                if(mType.equals(getResources().getString(R.string.admin))){
                                    if(email.equals(getResources().getString(R.string.adminid))){
                                        Intent studentIntent = new Intent(LoginAcitvity.this, OrderActivity.class);
                                        studentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(studentIntent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(LoginAcitvity.this, "you are not an Admin", Toast.LENGTH_SHORT).show();
                                    }
                            } else if(mType.equals(getResources().getString(R.string.user))){
                                    attachDatabaseReadListener(); }
                                else{
                                    Toast.makeText(LoginAcitvity.this, "you are not an Admin", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

    }

    private void attachDatabaseReadListener() {

        mUserID = mAuth.getUid();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("Users");

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if(Objects.equals(dataSnapshot.getKey(), mUserID)) {

                        User currentUser = dataSnapshot.getValue(User.class);

                        Toast.makeText(LoginAcitvity.this, "Login Successfull!", Toast.LENGTH_SHORT).show();

                            Intent studentIntent = new Intent(LoginAcitvity.this, ScannerActivity.class);
                            studentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(studentIntent);
                            finish();


                        return;

                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
        }
        mUserDatabaseReference.addChildEventListener(mChildEventListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(this, mAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
            if(Objects.equals(mAuth.getCurrentUser().getEmail(), getResources().getString(R.string.adminid))){
                Intent studentIntent = new Intent(LoginAcitvity.this, OrderActivity.class);
                studentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(studentIntent);
                finish();
            }
            else {
                Intent studentIntent = new Intent(LoginAcitvity.this, ScannerActivity.class);
                studentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(studentIntent);
                finish();
            }
        }
    }
}

