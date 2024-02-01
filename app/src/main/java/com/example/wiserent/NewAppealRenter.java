package com.example.wiserent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class NewAppealRenter extends AppCompatActivity {

    ImageButton bHomeBtn;
    Button bpaymentReq,bdateAppointment;
    User userObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appeal_renter);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");

        bHomeBtn = findViewById(R.id.homeBtn);
        bpaymentReq = findViewById(R.id.paymentReqBtn);
        bdateAppointment = findViewById(R.id.dateForFixerBtn);




        bHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent renterIntent = new Intent(getApplicationContext(), RenterHomePage.class);
                renterIntent.putExtra("user", userObj); // Pass the User object to RentedHomePage
                startActivity(renterIntent);
            }
        });
        bpaymentReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent payReqIntent = new Intent(getApplicationContext(), paymentRequest.class);
                payReqIntent.putExtra("user", userObj); // Pass the User object to PaymentRquest
                startActivity(payReqIntent);
            }
        });

        bdateAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent proAppointIntent = new Intent(getApplicationContext(), RenterHomePage.class);
                proAppointIntent.putExtra("user", userObj); // Pass the User object to RentedHomePage
                startActivity(proAppointIntent);
            }
        });
    }
}