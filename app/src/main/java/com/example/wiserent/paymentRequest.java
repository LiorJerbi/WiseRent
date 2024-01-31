package com.example.wiserent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    PaymentInfo paymentInfo;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_request);

        bhomeBtn = findViewById(R.id.homeBtn);
        bfinishBtn = findViewById(R.id.finishBtn);
        pDate = findViewById(R.id.paymentDate);
        pType = findViewById(R.id.paymentType);
        pAmount = findViewById(R.id.payAmount);
        fStore = FirebaseFirestore.getInstance();
        paymentInfo = new PaymentInfo();

        ArrayAdapter<CharSequence> dateAdapter = ArrayAdapter.createFromResource(this,R.array.months_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,R.array.bills_type, android.R.layout.simple_spinner_item);

        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        pDate.setAdapter(dateAdapter);
        pType.setAdapter(typeAdapter);

        pDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paymentInfo.setMonth(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        pType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paymentInfo.setBillType(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bfinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentInfo.setAmount(Double.parseDouble(pAmount.getText().toString().trim()));
                savePaymentInfoToFirebase(paymentInfo);
            }
        });

        bhomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RenterHomePage.class));
            }
        });
    }

    private void savePaymentInfoToFirebase(PaymentInfo pInfo){
        String userID = FirebaseAuth.getInstance().getUid();
        fStore.collection("users").document(userID).collection("payments").add(pInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(paymentRequest.this,"חיוב נוסף בהצלחה!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(paymentRequest.this,"שגיאה!" + e.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}