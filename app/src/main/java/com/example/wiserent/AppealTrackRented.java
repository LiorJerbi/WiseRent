package com.example.wiserent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AppealTrackRented extends AppCompatActivity {

    ImageButton buserBtn;
    User userObj;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appeal_track_rented);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");

        buserBtn = findViewById(R.id.userBtn);
        fStore = FirebaseFirestore.getInstance();

        // Call a method to dynamically populate the appeals table
        populateAppealsTable();

        buserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent renterIntent = new Intent(getApplicationContext(), RentedHomePage.class);
                renterIntent.putExtra("user", userObj); // Pass the User object to RentedHomePage
                startActivity(renterIntent);
            }
        });
    }

    private void populateAppealsTable() {
        // Get the TableLayout from the XML layout
        TableLayout tableLayout = findViewById(R.id.appealTableLayout);
        Log.d("AppealTrackRented", "User leased properties: " + userObj.getLeasedProperties());
        // Iterate through the user's leased properties
        for (Lease leasedProperty : userObj.getLeasedProperties()) {
            // Get the propertyId
            String propertyId = leasedProperty.getPropertyId();
            Log.d("AppealTrackRented", "Fetching appeals for property ID: " + leasedProperty.getPropertyId() + " PropertyIdString:" +propertyId);
            // Query appeals collection for appeals with the current propertyId
            fStore.collection("appeals")
                    .whereEqualTo("propertyId", propertyId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Handle the results of the query
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> appealData = document.getData();
                                // Determine the type of appeal
                                String appealType = (String) appealData.get("type");
                                // Create an Appeal object based on the type
                                Appeal appeal;
                                switch (appealType) {
                                    case "Bill":
                                        appeal = document.toObject(Bill.class);
                                        break;
                                    case "Professional Appointment":
                                        appeal = document.toObject(ProfessionalAppointment.class);
                                        break;
                                    case "GeneralProblem":
                                        appeal = document.toObject(GeneralProblem.class);
                                        break;
                                    default:
                                        appeal = null;
                                }

                                if (appeal != null) {
                                    // Fetch property name and renter name
                                    fetchPropertyName(leasedProperty.getPropertyId(), propertyName -> {
                                        if (propertyName != null) {
                                            // Create a new row and populate it with appeal details
                                            TableRow row = new TableRow(AppealTrackRented.this);
                                            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                                                    TableRow.LayoutParams.MATCH_PARENT,
                                                    TableRow.LayoutParams.WRAP_CONTENT
                                            );
                                            row.setLayoutParams(layoutParams);

                                            // Create TextViews for each column
                                            TextView addressTextView = new TextView(AppealTrackRented.this);
                                            TextView contentTextView = new TextView(AppealTrackRented.this);
                                            TextView statusTextView = new TextView(AppealTrackRented.this);
                                            TextView openedByTextView = new TextView(AppealTrackRented.this);

                                            // Set the text for each TextView
                                            addressTextView.setText(propertyName);
                                            contentTextView.setText(getContentText(appeal));
                                            statusTextView.setText(getStatusText(appeal));
                                            // Align the status text to the center
                                            statusTextView.setGravity(Gravity.CENTER); // This line aligns the text to the center

                                            getRenterName(appeal,leasedProperty.getRenterId(), renterName -> {
                                                if (renterName != null) {
                                                    openedByTextView.setText(renterName);
                                                } else {
                                                    openedByTextView.setText("Unknown Renter");
                                                }
                                                // Add TextViews to the row
                                                row.addView(openedByTextView);
                                                row.addView(contentTextView);
                                                row.addView(statusTextView);
                                                row.addView(addressTextView);

                                                if (appeal instanceof Bill || appeal instanceof ProfessionalAppointment) {
                                                    Button modifyButton = new Button(getApplicationContext()); // Use getApplicationContext() here
                                                    modifyButton.setText("עדכון פנייה");

                                                    // Set the button's layout parameters to make it smaller
                                                    TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                                                            TableRow.LayoutParams.WRAP_CONTENT,
                                                            TableRow.LayoutParams.WRAP_CONTENT
                                                    );
                                                    buttonLayoutParams.setMargins(4, 0, 4, 0); // Adjust margins as needed
                                                    modifyButton.setLayoutParams(buttonLayoutParams);

                                                    modifyButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            // Handle update for Bill or Professional Appointment appeal
                                                            if (appeal instanceof Bill) {
                                                                // Assuming updateStatus method is available in your class
                                                                updateStatus((Bill) appeal, document.getId());
                                                            } else if (appeal instanceof ProfessionalAppointment) {
                                                                // Assuming updateStatus method is available in your class
                                                                updateStatus((ProfessionalAppointment) appeal, document.getId());
                                                            }
                                                        }
                                                    });
                                                    row.addView(modifyButton);
                                                }

                                                // Add the row to the table
                                                tableLayout.addView(row);
                                            });
                                        }
                                    });
                                }
                            }
                        } else {
                            // Handle errors
                            Log.e("AppealTrackRenter", "Error getting appeals: ", task.getException());
                        }
                    });

        }
    }


    private String getContentText(Appeal appeal) {
        if (appeal instanceof Bill) {
            Bill bill = (Bill) appeal;
            return bill.getBillType();
        } else if (appeal instanceof ProfessionalAppointment) {
            ProfessionalAppointment profAppointment = (ProfessionalAppointment) appeal;
            return profAppointment.getProfessionalType();
        } else if (appeal instanceof GeneralProblem){
            GeneralProblem generalProblem = (GeneralProblem) appeal;
            return generalProblem.getContent();
        }
        else {
            return "Unknown Appeal Type";
        }
    }

    private void fetchPropertyName(String propertyId, Callback<String> callback) {
        fStore.collection("properties").document(propertyId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            callback.onCallback(document.getString("address"));
                        } else {
                            Log.d("AppealTrackRented", "Failed to fetch property name: Document does not exist");
                            callback.onCallback(null);
                        }
                    } else {
                        Log.d("AppealTrackRented", "Failed to fetch property name: Task not successful");
                        callback.onCallback(null);
                    }
                });
    }

    private void getRenterName(Appeal appeal, String renterId, Callback<String> callback) {
        if(appeal instanceof Bill || appeal instanceof ProfessionalAppointment){
        fStore.collection("users").document(renterId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            callback.onCallback(document.getString("fullName"));
                        } else {
                            Log.d("AppealTrackRented", "Failed to fetch renter name: Document does not exist");
                            callback.onCallback(null);
                        }
                    } else {
                        Log.d("AppealTrackRented", "Failed to fetch renter name: Task not successful");
                        callback.onCallback(null);
                    }
                });
        }
        else callback.onCallback(userObj.getFullName());
    }

    private String getOpenedByText(Appeal appeal,String rentedId){
        final String[] name = new String[1];
        if(appeal instanceof GeneralProblem){
            getNameRented(rentedId).addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if(task.isSuccessful()){
                        String fullName = task.getResult();
                        if(fullName != null){
                            name[0] = fullName;
                        }else{
                            Log.d("AppealTrackRenter", "full name import ERROR" );
                        }
                    }else {
                        Log.d("AppealTrackRenter", "full name import ERROR(not successful" );
                    }
                }
            });
            return name[0];
        }
        else{
            return userObj.getFullName();
        }
    }
    private Task<String> getNameRented(String rentedId){
        return fStore.collection("users").document(rentedId).get()
                .continueWith(new Continuation<DocumentSnapshot, String>() {
                    @Override
                    public String then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                return document.getString("fullName");
                            }else {
                                Log.d("AppealTrackRenter", "getRentedName ERROR");
                            }
                        }else {
                            Log.d("AppealTrackRenter", "getRentedName ERROR(not success");
                        }
                        return null;
                    }
                });
    }

    private String getStatusText(Appeal appeal) {
        if (appeal instanceof Bill) {
            return ((Bill) appeal).getStatus() ? "V" : "X";
        } else if (appeal instanceof ProfessionalAppointment) {
            return ((ProfessionalAppointment) appeal).isStatus() ? "V" : "X";
        } else if (appeal instanceof GeneralProblem) {
            return ((GeneralProblem) appeal).isStatus() ? "V" : "X";
        } else {
            return "N/A";
        }
    }

    // Interface for callback mechanism
    interface Callback<T> {
        void onCallback(T data);
    }
    private void updateStatus(Bill appeal, String documentId) {
        // Update the status of the appeal in Firestore
        appeal.setStatus(true); // Assuming setStatus method exists in Bill class

        // Update the status field in Firestore document
        fStore.collection("appeals").document(documentId).update("status", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Optionally, show a success message
                        Toast.makeText(AppealTrackRented.this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Log.e("AppealTrackRented", "Error updating status: " + e.getMessage());
                        // Optionally, show an error message
                        Toast.makeText(AppealTrackRented.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateStatus(ProfessionalAppointment appeal, String documentId) {
        // Update the status of the appeal in Firestore
        appeal.setStatus(true); // Assuming setStatus method exists in ProfessionalAppointment class

        // Update the status field in Firestore document
        fStore.collection("appeals").document(documentId).update("status", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Optionally, show a success message
                        Toast.makeText(AppealTrackRented.this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Log.e("AppealTrackRented", "Error updating status: " + e.getMessage());
                        // Optionally, show an error message
                        Toast.makeText(AppealTrackRented.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
