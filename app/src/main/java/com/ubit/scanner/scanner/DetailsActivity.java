package com.ubit.scanner.scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ubit.scanner.R;
import com.ubit.scanner.model.Order;
import com.ubit.scanner.utils.SharedPreferenceHandler;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private DatabaseReference mUserDatabaseReference;
    private FirebaseAuth mAuth;
    private TextView codeTextView, priceTextView;
    private ImageView dressImageView;
    private SharedPreferenceHandler mSharedPreferenceHandler;
    Spinner spin;
    String quantity;
    String[] bankNames={"1","2","3","4","5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        init();
    }

    private void init() {

        codeTextView = findViewById(R.id.code);
        priceTextView = findViewById(R.id.price);
        dressImageView = findViewById(R.id.dress_image);
         spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(DetailsActivity.this);
        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,bankNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        Button orderButton = findViewById(R.id.order);
        orderButton.setOnClickListener(this);

        mSharedPreferenceHandler = SharedPreferenceHandler.getInstance(this);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).child("name");

        codeTextView.setText(getIntent().getExtras().getString("code"));
        priceTextView.setText(getIntent().getExtras().getString("price"));
        Picasso.get().load(getIntent().getExtras().getString("imageUrl")).into(dressImageView);
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        //Toast.makeText(getApplicationContext(), bankNames[position], Toast.LENGTH_LONG).show();
        quantity=bankNames[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.order:

                mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.getValue(String.class);
                        String orderId = String.valueOf(mSharedPreferenceHandler.getOrderID());
                        Order order = new Order(orderId, mAuth.getUid(), name, codeTextView.getText().toString(),quantity);

                        DatabaseReference mOrderDatabaseReference = FirebaseDatabase.getInstance().getReference("Order").child(orderId);
                        mOrderDatabaseReference.setValue(order);

                        mSharedPreferenceHandler.setOrderID(mSharedPreferenceHandler.getOrderID() + 1);

                        DetailsActivity.super.onBackPressed();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                break;
        }
    }
}
