package com.ubit.scanner.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ubit.scanner.R;
import com.ubit.scanner.model.User;
import com.ubit.scanner.scanner.ScannerActivity;

public class Sign_up extends AppCompatActivity implements View.OnClickListener{

    private String mName;
    private String mEmail;
    private String mPassword;
    private String mPhone;
    private String mType;
    private String mUserID;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;

    private TextView mLoginTextView;
    private Button mSignUpButton;

    private EditText mNameEditText,mEmailEditText,mPasswordEditText,mPhoneEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("Users");

        mLoginTextView = (TextView) findViewById(R.id.textViewLogin);
        mEmailEditText = (EditText) findViewById(R.id.editTextEmail);
        mPasswordEditText = (EditText) findViewById(R.id.editTextPassword);
        mNameEditText = (EditText) findViewById(R.id.editTextName);
        mPhoneEditText = (EditText) findViewById(R.id.editTextPhone);
        mSignUpButton = (Button) findViewById(R.id.buttonSignUp);
        mLoginTextView.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.textViewLogin:

                Intent loginIntent = new Intent(Sign_up.this, LoginAcitvity.class);
                startActivity(loginIntent);
                finish();
                break;

            case R.id.buttonSignUp:
                registerUser();
                break;

        }
    }

    private void registerUser() {

        mName = mNameEditText.getText().toString().trim();
        mEmail = mEmailEditText.getText().toString().trim();
        mPassword = mPasswordEditText.getText().toString().trim();
        mPhone = mPhoneEditText.getText().toString().trim();

        if (mName.isEmpty()){

            mNameEditText.setError("Enter your Name!");
            mNameEditText.requestFocus();
            return;
        }

        if (mEmail.isEmpty()){

            mEmailEditText.setError("Enter your Email!");
            mEmailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()){

            mEmailEditText.setError("Invalid Email!");
            mEmailEditText.requestFocus();
            return;
        }

        if (mPassword.isEmpty()){
            mPasswordEditText.setError("Enter your Password!");
            mPasswordEditText.requestFocus();
            return;
        }

        if (mPassword.length()<6){
            mPasswordEditText.setError("Minimum Password Limit is 6");
            mPasswordEditText.requestFocus();
            return;
        }

        if (mPhone.isEmpty()){

            mPhoneEditText.setError("Enter your Phone!");
            mPhoneEditText.requestFocus();
            return;
        }
        if (mPhone.length() != 11){

            mPhoneEditText.setError("Invalid Number!");
            mPhoneEditText.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(mEmail,mPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            mUserID = mAuth.getUid();

                            User currentUser = new User (mName,mEmail,mPhone);

                            Toast.makeText(Sign_up.this, "Sign Up Successfull!", Toast.LENGTH_SHORT).show();

                                mUserDatabaseReference = mUserDatabaseReference.child(mUserID);
                                mUserDatabaseReference.setValue(currentUser);

                                Intent studentIntent = new Intent(Sign_up.this, ScannerActivity.class);
                                studentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(studentIntent);
                                finish();

                        }else{

                            if (task.getException() instanceof FirebaseAuthUserCollisionException){

                                Toast.makeText(getApplicationContext(),"Already Signed Up with this Email!",Toast.LENGTH_SHORT).show();
                            }else{

                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }
}

