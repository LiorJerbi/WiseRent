package com.example.wiserent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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
                    .whereEqualTo("propertyID", propertyId)
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
                                    openedByTextView.setText(getOpenedByText(appeal,ownedProperty.getRentedId()));

                                    // Add TextViews to the row
                                    row.addView(openedByTextView);
                                    row.addView(contentTextView);
                                    row.addView(statusTextView);
                                    row.addView(addressTextView);

                                    // Add the row to the table
                                    tableLayout.addView(row);
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
//            return "Bill - " + bill.getMonth() + " " + bill.getBillType();
            return bill.getBillType();
        } else if (appeal instanceof ProfessionalAppointment) {
            ProfessionalAppointment profAppointment = (ProfessionalAppointment) appeal;
//            return "Professional Appointment - " + profAppointment.getProfessionalType() + " on " + profAppointment.getDate();
            return profAppointment.getProfessionalType();
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
        }
        else {
            return "N/A";
        }
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

}