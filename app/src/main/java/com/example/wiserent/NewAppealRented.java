package com.example.wiserent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class NewAppealRented extends AppCompatActivity {
    EditText appealEditText;
    ImageButton buserBtn;
    Button bsendAppealBtn;
//    Spinner workerTypeSpinner;
    User userObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appeal_rented);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");
        buserBtn = findViewById(R.id.userBtn);
        bsendAppealBtn = findViewById(R.id.sendAppealBtn);
        appealEditText = findViewById(R.id.appealEditText);

//        // Populate the Spinner with the user's workers
//        ArrayAdapter<Property> propertyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userObj.getOwnedProperties());
//            propertyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            propertySpinner.setAdapter(propertyAdapter);



        buserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userbtn = new Intent(getApplicationContext(), RentedHomePage.class);
                userbtn.putExtra("user", userObj);
                startActivity(userbtn);
            }
        });

        bsendAppealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve appeal text from EditText
                String appealText = appealEditText.getText().toString();
                // Perform any actions with the appeal text, such as sending it to a server
            }
        });

    }
}