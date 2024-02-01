package com.example.wiserent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PremissionType extends AppCompatActivity {

    Button pRenterBtn,pRentedBtn;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premission_type);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        pRenterBtn = findViewById(R.id.RenterBtn);
        pRentedBtn = findViewById(R.id.RentedBtn);

        //moving to renter home page
        pRenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent renterIntent = new Intent(getApplicationContext(), RenterHomePage.class);
                renterIntent.putExtra("user", user); // Pass the User object to RenterHomePage
                startActivity(renterIntent);
            }
        });
        //moving to rented home page
        pRentedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rentedIntent = new Intent(getApplicationContext(), RentedHomePage.class);
                rentedIntent.putExtra("user", user); // Pass the User object to RentedHomePage
                startActivity(rentedIntent);
            }
        });
    }
}