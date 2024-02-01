package com.example.wiserent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.firestore.FirebaseFirestore;

public class NewAsset extends AppCompatActivity {

    User userObj;
    ImageButton bHomeBtn;
    EditText assetAddress, rentAmount;
    Button bSubmitBtn;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_asset);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");

        fStore = FirebaseFirestore.getInstance();

        bHomeBtn = findViewById(R.id.homeBtn);
        bSubmitBtn = findViewById(R.id.submitModiBtn);
        assetAddress = findViewById(R.id.newAssetAddress);
        rentAmount = findViewById(R.id.newRentAmount);

        bSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input values from the user
                String address = assetAddress.getText().toString().trim();
                double amount = Double.parseDouble(rentAmount.getText().toString().trim());

                // Create new property object
                Property property = new Property(null,address,amount,userObj.getUserId());

                // Save the property data to Firestore
                fStore.collection("properties")
                        .add(property)
                        .addOnSuccessListener(documentReference -> {
                            // Update the propertyId in the Property object
                            property.setPropertyId(documentReference.getId());

                            // Add the property to the user's list of owned properties
                            userObj.addOwnedProperty(property);
                            // Update the user data in Firestore
                            fStore.collection("users")
                                    .document(userObj.getUserId())
                                    .set(userObj)
                                    .addOnSuccessListener(aVoid -> {
                                        // Update the propertyId in the property document
                                        fStore.collection("properties")
                                                .document(documentReference.getId())
                                                .update("propertyId", documentReference.getId())
                                                .addOnSuccessListener(aVoid1 -> {
                                                    // Return to the RenterHomePage
                                                    Intent renterIntent = new Intent(getApplicationContext(), RenterHomePage.class);
                                                    renterIntent.putExtra("user", userObj);
                                                    startActivity(renterIntent);
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle the failure to update property data
                                                    Log.e("NewAsset", "Failed to update property data", e);
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle the failure to update user data
                                        Log.e("NewAsset", "Failed to update user data", e);
                                    });
                        })
                        .addOnFailureListener(e -> {
                            // Handle the failure to add property data
                            Log.e("NewAsset", "Failed to add property data", e);
                        });
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
    }
}