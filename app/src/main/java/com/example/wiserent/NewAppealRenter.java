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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appeal_renter);

        bHomeBtn = findViewById(R.id.homeBtn);
        bpaymentReq = findViewById(R.id.paymentReqBtn);
        bdateAppointment = findViewById(R.id.dateForFixerBtn);






        bHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RenterHomePage.class));
            }
        });
        bpaymentReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), paymentRequest.class));
            }
        });

        bdateAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RenterHomePage.class));
            }
        });
    }
}