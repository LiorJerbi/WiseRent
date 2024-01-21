package com.example.wiserent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PremissionType extends AppCompatActivity {

    Button pRenterBtn,pRentedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premission_type);

        pRenterBtn = findViewById(R.id.RenterBtn);
        pRentedBtn = findViewById(R.id.RentedBtn);

        //moving to renter home page
        pRenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RenterHomePage.class));
            }
        });
        //moving to rented home page
        pRentedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RentedHomePage.class));
            }
        });
    }
}