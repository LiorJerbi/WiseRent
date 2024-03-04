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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;

public class RentedPaymentPage extends AppCompatActivity {

    ImageButton bUserBtn;
    User userObj;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rented_payment_page);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");
        bUserBtn = findViewById(R.id.userBtn);
        fStore = FirebaseFirestore.getInstance();

        bUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userbtn = new Intent(getApplicationContext(), RentedHomePage.class);
                userbtn.putExtra("user", userObj);
                startActivity(userbtn);
            }
        });

        // Call a method to dynamically populate the payment table with bill type appeals
        populatePaymentTable();
    }

    private void populatePaymentTable() {
        // Get the TableLayout from the XML layout
        TableLayout tableLayout = findViewById(R.id.appealTableLayout);

        // Iterate through the properties leased by the user
        for (Lease leasedProperty : userObj.getLeasedProperties()) {
            // Get the propertyId
            String propertyId = leasedProperty.getPropertyId();

            // Query the appeals collection for bill type appeals associated with the current propertyId
            fStore.collection("appeals")
                    .whereEqualTo("type", "Bill")
                    .whereEqualTo("propertyId", propertyId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Handle the results of the query
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> appealData = document.getData();
                                // Create a Bill object from the document data
                                Bill bill = document.toObject(Bill.class);
                                if (bill != null) {
                                    // Create a new row and populate it with bill details
                                    TableRow row = new TableRow(RentedPaymentPage.this);
                                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                                            TableRow.LayoutParams.MATCH_PARENT,
                                            TableRow.LayoutParams.WRAP_CONTENT
                                    );
                                    row.setLayoutParams(layoutParams);

                                    // Create TextViews for each column
                                    Button paymentButton = new Button(RentedPaymentPage.this);
                                    TextView amountTextView = new TextView(RentedPaymentPage.this);
                                    TextView monthTextView = new TextView(RentedPaymentPage.this);
                                    TextView typeTextView = new TextView(RentedPaymentPage.this);

                                    // Set layout parameters and gravity for TextViews
                                    TableRow.LayoutParams textViewParams = new TableRow.LayoutParams(
                                            0,
                                            TableRow.LayoutParams.WRAP_CONTENT,
                                            1
                                    );
                                    textViewParams.gravity = Gravity.END; // Shift text to the right
                                    amountTextView.setLayoutParams(textViewParams);
                                    monthTextView.setLayoutParams(textViewParams);
                                    typeTextView.setLayoutParams(textViewParams);

                                    // Set the text alignment for each TextView
                                    amountTextView.setGravity(Gravity.END);
                                    monthTextView.setGravity(Gravity.END);
                                    typeTextView.setGravity(Gravity.END);

                                    // Set the text for each TextView
                                    paymentButton.setText("תשלום");
                                    amountTextView.setText(String.valueOf(bill.getAmount()));
                                    monthTextView.setText(bill.getMonth());
                                    typeTextView.setText(bill.getBillType());

                                    monthTextView.setGravity(Gravity.CENTER);

                                    // Add OnClickListener to the payment button
                                    paymentButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Handle payment button click
//                                            Intent paymentIntent = new Intent(RentedPaymentPage.this, PaymentActivity.class);
//                                            paymentIntent.putExtra("bill", bill); // Pass bill object to payment activity
//                                            startActivity(paymentIntent);
                                        }
                                    });

                                    // Add TextViews to the row
                                    row.addView(paymentButton);
                                    row.addView(amountTextView);
                                    row.addView(monthTextView);
                                    row.addView(typeTextView);


                                    // Add the row to the table
                                    tableLayout.addView(row);
                                }
                            }
                        } else {
                            // Handle errors
                            Log.e("RentedPaymentPage", "Error getting bill type appeals for property ID " + propertyId + ": ", task.getException());
                        }
                    });
        }
    }
}
