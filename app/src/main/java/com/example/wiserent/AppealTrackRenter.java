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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AppealTrackRenter extends AppCompatActivity {

    ImageButton bhomeBtn;
    User userObj;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appeal_track_renter);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");

        bhomeBtn = findViewById(R.id.homeBtn);
        fStore = FirebaseFirestore.getInstance();

        // Call a method to dynamically populate the appeals table
        populateAppealsTable();

        bhomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent renterIntent = new Intent(getApplicationContext(), RenterHomePage.class);
                renterIntent.putExtra("user", userObj); // Pass the User object to RentedHomePage
                startActivity(renterIntent);
            }
        });
    }

    private void populateAppealsTable() {
        // Get the TableLayout from the XML layout
        TableLayout tableLayout = findViewById(R.id.appealTableLayout);

        // Iterate through the user's owned properties
        for (Property ownedProperty : userObj.getOwnedProperties()) {
            // Get the propertyId
            String propertyId = ownedProperty.getPropertyId();

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
                                        appeal = null; // Handle other types as needed
                                }

                                if (appeal != null) {
                                    // Create a new row and populate it with appeal details
                                    TableRow row = new TableRow(this);
                                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                                            TableRow.LayoutParams.MATCH_PARENT,
                                            TableRow.LayoutParams.WRAP_CONTENT
                                    );
                                    row.setLayoutParams(layoutParams);

                                    // Create TextViews for each column
                                    TextView addressTextView = new TextView(this);
                                    TextView contentTextView = new TextView(this);
                                    TextView statusTextView = new TextView(this);
                                    TextView openedByTextView = new TextView(this);

                                    // Set the text for each TextView
                                    addressTextView.setText(ownedProperty.getAddress());
                                    contentTextView.setText(getContentText(appeal));
                                    statusTextView.setText(getStatusText(appeal));

                                    // Align the status text to the center
                                    statusTextView.setGravity(Gravity.CENTER); // This line aligns the text to the center

                                    // Call getRenterName to retrieve renter name
                                    getRenterName(appeal, ownedProperty.getRentedId(), renterName -> {
                                        if (renterName != null) {
                                            // Set the renter name to the TextView
                                            openedByTextView.setText(renterName);
                                        } else {
                                            openedByTextView.setText("Unknown Renter");
                                        }

                                        // Add TextViews to the row
                                        row.addView(openedByTextView);
                                        row.addView(contentTextView);
                                        row.addView(statusTextView);
                                        row.addView(addressTextView);

                                        // Add the row to the table
                                        tableLayout.addView(row);

                                        // Add a Button to modify the status for GeneralProblem appeals
                                        if (appeal instanceof GeneralProblem) {
                                            Button modifyButton = new Button(this);
                                            modifyButton.setText("עדכון");

                                            // Set the button's layout parameters to make it smaller
                                            TableRow.LayoutParams buttonLayoutParams = new TableRow.LayoutParams(
                                                    TableRow.LayoutParams.WRAP_CONTENT,
                                                    TableRow.LayoutParams.WRAP_CONTENT
                                            );
                                            buttonLayoutParams.setMargins(4, 0, 4, 0);
                                            modifyButton.setLayoutParams(buttonLayoutParams);

                                            modifyButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    updateStatus((GeneralProblem) appeal, document.getId());
                                                }
                                            });
                                            row.addView(modifyButton);
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
        } else if (appeal instanceof GeneralProblem) {
            GeneralProblem generalProblem = (GeneralProblem) appeal;
            return generalProblem.getContent();
        } else {
            return "Unknown Appeal Type";
        }
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

    private void getRenterName(Appeal appeal, String renterId, Callback<String> callback) {
        if (appeal instanceof GeneralProblem) {
            fStore.collection("users").document(renterId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                callback.onCallback(document.getString("fullName"));
                            } else {
                                Log.d("AppealTrackRenter", "Failed to fetch renter name: Document does not exist");
                                callback.onCallback(null);
                            }
                        } else {
                            Log.d("AppealTrackRenter", "Failed to fetch renter name: Task not successful");
                            callback.onCallback(null);
                        }
                    });
        } else {
            callback.onCallback(userObj.getFullName());
        }
    }

    // Interface for callback mechanism
    interface Callback<T> {
        void onCallback(T data);
    }
    private void updateStatus(GeneralProblem appeal, String documentId) {
        // Update the status of the appeal in Firestore
        appeal.setStatus(true); // Assuming setStatus method exists in GeneralProblem class

        // Update the status field in Firestore document
        fStore.collection("appeals").document(documentId).update("status", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Update the UI to reflect the change if necessary
//                        statusTextView.setText("V");
                        Toast.makeText(AppealTrackRenter.this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Log.e("AppealTrackRenter", "Error updating status: " + e.getMessage());
                        Toast.makeText(AppealTrackRenter.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
