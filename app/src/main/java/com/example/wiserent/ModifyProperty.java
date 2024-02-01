package com.example.wiserent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class ModifyProperty extends AppCompatActivity {

    private User userObj;
    private Property selectedProperty;
    private ImageButton homeBtn;
    private EditText newAssetAddress, newRentAmount;
    private Button submitModiBtn;
    private FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_property);

        // Retrieve the User and selected Property objects passed from the previous activity
        userObj = (User) getIntent().getSerializableExtra("user");
        selectedProperty = (Property) getIntent().getSerializableExtra("property");
        // Log details of the received selectedProperty
        Log.d("ModifyProperty", "Selected property details - Address: " + selectedProperty.getAddress() +
                ", Rent Amount: " + selectedProperty.getRentAmount() +
                ", Property ID: " + selectedProperty.getPropertyId());

        // Initialize FirebaseFirestore
        fStore = FirebaseFirestore.getInstance();

        homeBtn = findViewById(R.id.homeBtn);
        newAssetAddress = findViewById(R.id.newAssetAddress);
        newRentAmount = findViewById(R.id.newRentAmount);
        submitModiBtn = findViewById(R.id.submitModiBtn);

        // Display current property details as hints in EditText fields
        newAssetAddress.setHint(selectedProperty.getAddress());
        newRentAmount.setHint(String.valueOf(selectedProperty.getRentAmount()));

        // Set click listener for the home button
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent renterIntent = new Intent(getApplicationContext(), RenterHomePage.class);
                renterIntent.putExtra("user", userObj); // Pass the User object to RentedHomePage
                startActivity(renterIntent);
            }
        });

        // Set click listener for the submit modification button
        submitModiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyProperty();
            }
        });


    }

    private void modifyProperty() {
        // Get new property details from the EditText fields
        String newAddress = newAssetAddress.getText().toString().trim();
        String newAmountText = newRentAmount.getText().toString().trim();
        double newAmount;


        //checking for null fields
        if(TextUtils.isEmpty(newAddress)){
            newAssetAddress.setError("שדה כתובת ריק");
            return; // Stop the modification process if address is empty
        }
        //checking for null fields
        if(TextUtils.isEmpty(newAmountText)){
            newAmount = 0.0;
            newRentAmount.setError("שדה שכירות ריק");
            return; // Stop the modification process if rent amount is empty
        }else {
            try {
                // Convert the String to double for further processing
                newAmount = Double.parseDouble(newAmountText);

                // Additional validation to ensure rent amount is non-negative
                if (newAmount < 0) {
                    newRentAmount.setError("שדה שכירות חייב להיות מספר חיובי");
                    return; // Stop the modification process if rent amount is negative
                }
            } catch (NumberFormatException e) {
                // Handle the case where the rent amount is not a valid number
                newRentAmount.setError("שדה שכירות חייב להיות מספר");
                return; // Stop the modification process if rent amount is not a valid number
            }
        }



        // Update property details in the selectedProperty object
        selectedProperty.setAddress(newAddress);
        selectedProperty.setRentAmount(newAmount);

        // Update property details in Firestore under the "properties" collection
        fStore.collection("properties")
                .document(selectedProperty.getPropertyId())
                .update("address", newAddress, "rentAmount", newAmount)
                .addOnSuccessListener(aVoid -> {
                    // Property details updated successfully
                    Log.d("ModifyProperty", "Firestore update success");

                    // Update property details in the user's list of owned properties
                    userObj.editOwnedProperty(selectedProperty.getPropertyId(), newAddress, newAmount);
                    // Update the user data in Firestore after updating property details
                    fStore.collection("users")
                            .document(userObj.getUserId())
                            .update("ownedProperties", userObj.getOwnedProperties())
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(ModifyProperty.this, "נכס נערך בהצלחה!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Handle the failure to update user data
                                Log.e("ModifyProperty", "Failed to update user data", e);
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to update property details in Firestore
                    Log.e("ModifyProperty", "Failed to update property details", e);
                    Toast.makeText(ModifyProperty.this, "ניסיון לעריכת נכס נכשלה!", Toast.LENGTH_SHORT).show();
                });
    }

}