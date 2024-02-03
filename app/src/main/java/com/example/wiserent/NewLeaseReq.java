package com.example.wiserent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NewLeaseReq extends AppCompatActivity {

    private EditText etPropertyAddress;
    private ImageButton buserBtn;
    private Button btnSearch;
    private RecyclerView recyclerView;
    private PropertyAdapter propertyAdapter;

    private FirebaseFirestore fStore;

    User userObj;
    String selectedPropertyId; // Variable to store the selected propertyId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lease_req);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");

        etPropertyAddress = findViewById(R.id.etPropertyAddress);
        btnSearch = findViewById(R.id.btnSearch);
        recyclerView = findViewById(R.id.recyclerView);
        buserBtn = findViewById(R.id.userBtn);
        fStore = FirebaseFirestore.getInstance();

        propertyAdapter = new PropertyAdapter(new ArrayList<>(), new PropertyAdapter.PropertyClickListener() {
            @Override
            public void onPropertyClick(String propertyId) {
                // Handle property click, store the propertyId

                selectedPropertyId = propertyId;
                Toast.makeText(NewLeaseReq.this, "הנכס הנבחר "+selectedPropertyId, Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(propertyAdapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchProperty();
            }
        });

        buserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userbtn = new Intent(getApplicationContext(), RentedHomePage.class);
                userbtn.putExtra("user", userObj);
                startActivity(userbtn);
            }
        });

        // Button for sending lease request
        Button sendReqButton = findViewById(R.id.sendReqBtn);
        sendReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPropertyId != null) {
                    sendLeaseRequest();
                } else {
                    Toast.makeText(NewLeaseReq.this, "Please select a property first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchProperty() {
        String address = etPropertyAddress.getText().toString().trim();

        // Query the properties collection based on the entered address
        fStore.collection("properties")
                .whereEqualTo("address", address)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Property> searchResults = new ArrayList<>();

                            // Iterate through the query results
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Property property = document.toObject(Property.class);
                                searchResults.add(property);
                            }

                            // Update the RecyclerView with search results
                            updateRecyclerView(searchResults);
                        } else {
                            // Handle errors
                            Toast.makeText(NewLeaseReq.this, "Error searching for property", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateRecyclerView(List<Property> searchResults) {
        // Update the RecyclerView with the new search results
        propertyAdapter.updateData(searchResults);
    }

    private void sendLeaseRequest() {
        // Create a new lease object
        Lease lease = new Lease(
                null, // Auto-generated leaseId by Firebase
                selectedPropertyId,
                null, // Initial renterId is null (lessorId)
                userObj.getUserId(), // Set rentedId to the user's ID
                false // Initial status is false (pending approval)
        );

        // Add the lease to the "Leases" collection
        fStore.collection("leases")
                .add(lease)
                .addOnSuccessListener(documentReference -> {
                    String leaseId = documentReference.getId();
                    lease.setLeaseId(leaseId);

                    //update the new lease with generated leaseid
                    fStore.collection("leases").document(leaseId)
                            .set(lease)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(NewLeaseReq.this, "Lease request sent successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Handle errors
                                Toast.makeText(NewLeaseReq.this, "Error sending lease request", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewLeaseReq.this, "Error sending lease request", Toast.LENGTH_SHORT).show();;
                });

                // Add the lease request to the "requests" collection
        fStore.collection("requests")
                .add(lease)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(NewLeaseReq.this, "Lease request sent successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewLeaseReq.this, "Error sending lease request", Toast.LENGTH_SHORT).show();
                });
    }
}