package com.example.wiserent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RenterLeasePage extends AppCompatActivity {

    private ImageButton  bHomeBtn;
    private FirebaseFirestore fStore;
    private User userObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_lease_page);

        // Retrieve the User object passed from the previous activity
        userObj = (User) getIntent().getSerializableExtra("user");


        bHomeBtn = findViewById(R.id.homeBtn);
        fStore = FirebaseFirestore.getInstance();

        // Call a method to dynamically populate the table with lease requests
        populateLeaseRequestsTable();

        bHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent renterIntent = new Intent(getApplicationContext(), RenterHomePage.class);
                renterIntent.putExtra("user", userObj); // Pass the User object to RentedHomePage
                startActivity(renterIntent);
            }
        });
    }

    private void populateLeaseRequestsTable() {
        // Get the TableLayout from the XML layout
        TableLayout tableLayout = findViewById(R.id.tableLayout);



        List<String> propertyIds = new ArrayList<>();
        for (Property property : userObj.getOwnedProperties()) {
            propertyIds.add(property.getPropertyId());
        }


        // Query the "requests" collection in Firebase
        fStore.collection("leases")
                .whereIn("propertyId", propertyIds)
                .whereEqualTo("renterId", null)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Handle the results of the query
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Lease lease = document.toObject(Lease.class);
                                // Create a new row for each lease request
                                TableRow row = new TableRow(RenterLeasePage.this);

                                // Create TextViews for each column
                                TextView rentedNameTextView = new TextView(RenterLeasePage.this);
                                TextView propertyAddressTextView = new TextView(RenterLeasePage.this);
                                Button enableButton = new Button(RenterLeasePage.this);
                                Button denyButton = new Button(RenterLeasePage.this);

                                // Set the text for TextViews
                                getRentedName(lease.getRentedId()).addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if(task.isSuccessful()){
                                            String fullName = task.getResult();
                                            if(fullName != null){
                                                rentedNameTextView.setText(fullName);
                                            }else{
                                                Log.d("RenterLeasePage", "full name import ERROR" );
                                            }
                                        }else {
                                            Log.d("RenterLeasePage", "full name import ERROR(not successful" );
                                        }
                                    }
                                });

                                getPropertyAddress(lease.getPropertyId()).addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if(task.isSuccessful()){
                                            String address = task.getResult();
                                            if(address != null){
                                                propertyAddressTextView.setText(address);
                                            }else{
                                                Log.d("RenterLeasePage", "address import ERROR" );
                                            }
                                        }else {
                                            Log.d("RenterLeasePage", "address import ERROR(not successful" );
                                        }
                                    }
                                });
                                // Set the text for buttons
                                enableButton.setText("אפשר");
                                denyButton.setText("דחה");

                                // Set onClickListener for enable button
                                enableButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Handle enabling the lease (update Firebase document)
                                        enableLease(lease);
                                        refreshUI(row);
                                    }
                                });

                                // Set onClickListener for deny button
                                denyButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Handle denying the lease (delete Firebase document)
                                        denyLease(lease);
                                        refreshUI(row);
                                    }
                                });

                                // Add TextViews and Buttons to the row
                                row.addView(denyButton);
                                row.addView(enableButton);
                                row.addView(propertyAddressTextView);
                                row.addView(rentedNameTextView);

                                // Add the row to the table
                                tableLayout.addView(row);
                            }
                        } else {
                            // Handle errors
                        }
                    }
                });
    }

    private Task<String> getRentedName(String rentedId) {
        return fStore.collection("users").document(rentedId).get()
                .continueWith(new Continuation<DocumentSnapshot, String>() {
                    @Override
                    public String then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                return document.getString("fullName");
                            }else {
                                Log.d("RenterLeasePage", "getRentedName ERROR");
                            }
                        }else {
                            Log.d("RenterLeasePage", "getRentedName ERROR(not success");
                        }
                        return null;
                    }
                });
    }

    private Task<String> getPropertyAddress(String propertyId) {
        return fStore.collection("properties").document(propertyId).get()
                .continueWith(new Continuation<DocumentSnapshot, String>() {
                    @Override
                    public String then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                return document.getString("address");
                            }else {
                                Log.d("RenterLeasePage", "getPropertyAddress ERROR(not success");
                            }
                        }else {
                            Log.d("RenterLeasePage", "getPropertyAddress ERROR(not success");
                        }
                        return null;
                    }
                });
    }

    private void enableLease(Lease lease) {

        updateLeaseInFirebase(lease.getLeaseId(), true, userObj.getUserId(),lease.getPropertyId(),lease.getRentedId());
    }
    private void updateLeaseInFirebase(String leaseId, boolean status, String renterId,String propertyId,String rentedId) {
        fStore.collection("leases").document(leaseId)
                .update("status", status, "renterId", renterId)
                .addOnSuccessListener(aVoid -> {
                    // Lease updated successfully, now update the associated property
                    updatePropertyInFirebase(propertyId,rentedId);
                    updateUserOwnedProperties(rentedId, propertyId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(RenterLeasePage.this, "שגיאה באישור הקישור", Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePropertyInFirebase(String propertyId,String rentedId) {

        fStore.collection("properties").document(propertyId)
                .update("rentedId", rentedId)
                .addOnSuccessListener(aVoid -> {
                    // Property updated successfully
                    Toast.makeText(RenterLeasePage.this, "הפרטים עודכנו בהצלחה", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(RenterLeasePage.this, "שגיאה באישור הקישור", Toast.LENGTH_SHORT).show();
                });
    }
    private void updateUserOwnedProperties(String rentedId, String propertyId) {
        // Update the user's (renterId) ownedProperties with the new rentedId
        List<Property> ownedProperties = userObj.getOwnedProperties();
        for (Property property : ownedProperties) {
            if (property.getPropertyId().equals(propertyId)) {
                property.setRentedId(rentedId);
                break; // No need to continue once the property is found and updated
            }
        }

        // Update the user object in Firebase (if needed)
        fStore.collection("users").document(userObj.getUserId())
                .update("ownedProperties", ownedProperties)
                .addOnSuccessListener(aVoid -> {
                    // User's ownedProperties updated successfully
                    // You can perform additional actions here if needed
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(RenterLeasePage.this, "שגיאה בעדכון הנכס בבעלות המשתמש", Toast.LENGTH_SHORT).show();
                });
    }



    private void refreshUI(TableRow row) {
        // Get the TableLayout from the XML layout
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        // Remove the TableRow from the TableLayout
        tableLayout.removeView(row);
    }



    private void denyLease(Lease lease) {
        fStore.collection("leases").document(lease.getLeaseId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Handle the success case
                    Toast.makeText(RenterLeasePage.this, "סירוב בוצע בהצלחה", Toast.LENGTH_SHORT).show();
                    // You may want to refresh your UI or perform additional actions here
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(RenterLeasePage.this, "שגיאה בסירוב", Toast.LENGTH_SHORT).show();
                });
    }


}