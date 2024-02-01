package com.example.wiserent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class AssetCustomize extends AppCompatActivity {

    User userObj;
    ImageButton bHomeBtn;
    Button bNewAssetBtn, bDeletePropertyBtn, bModifyPropertyBtn;
    Spinner propertySpinner;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_customize);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");

        fStore = FirebaseFirestore.getInstance();

        bHomeBtn = findViewById(R.id.homeBtn);
        bNewAssetBtn = findViewById(R.id.newAssetBtn);
        bDeletePropertyBtn = findViewById(R.id.deletePropertyBtn);
        bModifyPropertyBtn = findViewById(R.id.modifyPropertyBtn);
        propertySpinner = findViewById(R.id.propertySpinner);

        // Populate the spinner with user-owned properties
        ArrayAdapter<Property> propertyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userObj.getOwnedProperties());
        propertyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        propertySpinner.setAdapter(propertyAdapter);

        // Spinner item selected listener
        propertySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Enable or disable buttons based on whether a property is selected
                boolean propertySelected = position != AdapterView.INVALID_POSITION;
                bDeletePropertyBtn.setEnabled(propertySelected);
                bModifyPropertyBtn.setEnabled(propertySelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });


        // Button click listeners
        bHomeBtn.setOnClickListener(v -> navigateToHomePage());
        bNewAssetBtn.setOnClickListener(v -> navigateToNewAssetPage());
        bDeletePropertyBtn.setOnClickListener(v -> deleteSelectedProperty());
        bModifyPropertyBtn.setOnClickListener(v -> modifySelectedProperty());


    }

    private void navigateToHomePage() {
        Intent renterIntent = new Intent(getApplicationContext(), RenterHomePage.class);
        renterIntent.putExtra("user", userObj); // Pass the User object to RentedHomePage
        startActivity(renterIntent);
    }

    private void navigateToNewAssetPage() {
        Intent newAssetIntent = new Intent(getApplicationContext(), NewAsset.class);
        newAssetIntent.putExtra("user", userObj); // Pass the User object to New Asset page
        startActivity(newAssetIntent);
    }

    private void deleteSelectedProperty() {
        // Get the selected property from the spinner
        Property selectedProperty = (Property) propertySpinner.getSelectedItem();

        if (selectedProperty != null) {
            // Remove the property from the user's list of owned properties
            userObj.removeOwnedProperty(selectedProperty.getPropertyId());

            // Update the user data in Firestore (remove the property from the user's owned properties)
            fStore.collection("users")
                    .document(userObj.getUserId())
                    .update("ownedProperties", userObj.getOwnedProperties())
                    .addOnSuccessListener(aVoid -> {
                        // User data updated successfully

                        // Delete the property from the "properties" collection
                        fStore.collection("properties")
                                .document(selectedProperty.getPropertyId())
                                .delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    // Property deleted successfully
                                    Toast.makeText(AssetCustomize.this,"נכס נמחק בהצלחה!",Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure to delete the property
                                    Log.e("AssetCustomize", "Failed to delete property", e);
                                });

                        // Notify the spinner adapter about the change in data
                        ArrayAdapter<Property> adapter = (ArrayAdapter<Property>) propertySpinner.getAdapter();
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to update user data
                        Log.e("AssetCustomize", "Failed to update user data", e);
                    });
        }

    }

    private void modifySelectedProperty() {
        // Get the selected property from the spinner
        Property selectedProperty = (Property) propertySpinner.getSelectedItem();

        if (selectedProperty != null) {
            // Create an intent to launch the ModifyProperty activity
            Log.d("AssetCustomize", "Selected property details - Address: " + selectedProperty.getAddress() +
                    ", Rent Amount: " + selectedProperty.getRentAmount() +
                    ", Property ID: " + selectedProperty.getPropertyId());
            Intent modifyIntent = new Intent(getApplicationContext(), ModifyProperty.class);
            modifyIntent.putExtra("user", userObj);
            modifyIntent.putExtra("property", selectedProperty);
            startActivity(modifyIntent);
        }
    }
}