package com.example.wiserent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class paymentRequest extends AppCompatActivity {

    ImageButton bhomeBtn;
    Button bfinishBtn;
    Spinner pType, pDate;
    EditText pAmount;
    Bill bill;
    User userObj;
    Property selectedProperty;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_request);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");
        selectedProperty = (Property) intent.getSerializableExtra("selectedProperty");


        bhomeBtn = findViewById(R.id.homeBtn);
        bfinishBtn = findViewById(R.id.finishBtn);
        pDate = findViewById(R.id.paymentDate);
        pType = findViewById(R.id.paymentType);
        pAmount = findViewById(R.id.payAmount);
        fStore = FirebaseFirestore.getInstance();
        bill = new Bill();

        ArrayAdapter<CharSequence> dateAdapter = ArrayAdapter.createFromResource(this,R.array.months_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,R.array.bills_type, android.R.layout.simple_spinner_item);

        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        pDate.setAdapter(dateAdapter);
        pType.setAdapter(typeAdapter);

        //extracting the bill month user clicked on
        pDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bill.setMonth(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //extracting the Bill type user clicked on
        pType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bill.setBillType(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        bfinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for empty or inappropriate data
                if (TextUtils.isEmpty(pAmount.getText().toString().trim())) {
                    Toast.makeText(paymentRequest.this, "הזן סכום לתשלום", Toast.LENGTH_SHORT).show();
                } else {
                    bill.setAmount(Double.parseDouble(pAmount.getText().toString().trim()));
                    savePaymentInfoToFirebase(bill,selectedProperty);
                }
            }
        });

        bhomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent renterIntent = new Intent(getApplicationContext(), RenterHomePage.class);
                renterIntent.putExtra("user", userObj); // Pass the User object to RentedHomePage
                startActivity(renterIntent);
            }
        });
    }

    private void savePaymentInfoToFirebase(Bill pInfo, Property selectedProperty) {
        // Create a new appeal document under the "appeals" collection
        DocumentReference appealReference = fStore.collection("appeals").document();

        // Set the appealId to the generated document ID
        pInfo.setAppealId(appealReference.getId());

        // Set the propertyId in the appeal
        pInfo.setPropertyID(selectedProperty.getPropertyId());

        // Add the Bill to the user's local list of appeals
        userObj.addAppeal(pInfo);

        // Update the user's appeal list in the user document
        updateAppealListInUserDocument(userObj);

        // Save the Bill to the appeals collection
        appealReference.set(pInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(paymentRequest.this, "חיוב נוסף בהצלחה!", Toast.LENGTH_SHORT).show();
                        Log.d("PaymentRequest", "Appeal stored in separate collection, user appeal list updated locally and in the database");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(paymentRequest.this, "שגיאה!" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAppealListInUserDocument(User user) {
        // Get the user's document reference
        DocumentReference userReference = fStore.collection("users").document(user.getUserId());

        // Update the user's appeal list in the user document
        userReference.update("appeals", user.getAppeals())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // The user's appeal list has been successfully updated in Firebase
                        Log.d("PaymentRequest", "User appeal list updated successfully in the database");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("PaymentRequest", "Failed to update user appeal list in the database", e);
                    }
                });
    }

}