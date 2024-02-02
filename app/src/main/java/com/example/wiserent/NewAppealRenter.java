package com.example.wiserent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

public class NewAppealRenter extends AppCompatActivity {

    ImageButton bHomeBtn;
    Button bpaymentReq,bdateAppointment;
    Spinner propertySpinner;
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
        propertySpinner = findViewById(R.id.propertySpinner);


        // Populate the Spinner with the user's properties
        ArrayAdapter<Property> propertyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userObj.getOwnedProperties());
        propertyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        propertySpinner.setAdapter(propertyAdapter);

        // Set a listener for Spinner item selection
        propertySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected property
                Property selectedProperty = (Property) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

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
                payReqIntent.putExtra("selectedProperty", (Property) propertySpinner.getSelectedItem());
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