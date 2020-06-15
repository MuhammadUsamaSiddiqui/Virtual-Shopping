package com.ubit.scanner.Order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ubit.scanner.Auth.LoginAcitvity;
import com.ubit.scanner.Auth.Sign_up;
import com.ubit.scanner.R;
import com.ubit.scanner.model.Dress;
import com.ubit.scanner.model.Order;
import com.ubit.scanner.model.User;
import com.ubit.scanner.scanner.ScannerActivity;

import java.util.Objects;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference,mOrderDatabaseReference;
    private ChildEventListener mChildEventListener;
    private EditText orderid;
    private String mUserID,orderID;
    private String mType;
    private Button buttonsearch;
    private TextView codeTextView, priceTextView,quantity;
    private ImageView dressImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        codeTextView = findViewById(R.id.code);
        priceTextView = findViewById(R.id.price);
        dressImageView = findViewById(R.id.dress_image);
        quantity= findViewById(R.id.quantity);
        orderid=findViewById(R.id.orderid);
        buttonsearch = (Button) findViewById(R.id.buttonsearch);
        buttonsearch.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonsearch:
                orderID=orderid.getText().toString();
                search();
               break;
        }
    }

    private void search() {
        attachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mUserDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void attachDatabaseReadListener() {
        mUserID = mAuth.getUid();
        mUserDatabaseReference=mFirebaseDatabase.getReference().child("Order").child(orderID);
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Order order = dataSnapshot.getValue(Order.class);
                mOrderDatabaseReference=mFirebaseDatabase.getReference().child("Dress").child(Objects.requireNonNull(order).getDressId());
                mOrderDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Dress dress=dataSnapshot.getValue(Dress.class);
                        if(dress!=null){
                            codeTextView.setText(dress.getCode());
                            priceTextView.setText(dress.getPrice());
                            quantity.setText(order.getQuantity());
                            Picasso.get().load(dress.getImage()).into(dressImageView);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginAcitvity.class));
        finish();
    }
    }



