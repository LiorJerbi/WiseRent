package com.example.wiserent;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class RentedPaymentPage extends AppCompatActivity {

    ImageButton buserBtn;
    Button bpayment1Btn, bpayment2Btn ,bpayment3Btn;
    User userObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rented_payment_page);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");
        buserBtn = findViewById(R.id.userBtn);
        bpayment1Btn = findViewById(R.id.payment1Btn);
        bpayment2Btn = findViewById(R.id.payment2Btn);
        bpayment3Btn = findViewById(R.id.payment3Btn);

        buserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RentedHomePage.class));
            }
        });

        bpayment1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle payment for row 1
            }
        });

        bpayment2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle payment for row 2
            }
        });

        bpayment3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle payment for row 3
            }
        });
    }
}
