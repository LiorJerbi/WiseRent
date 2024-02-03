package com.example.wiserent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class RentedHomePage extends AppCompatActivity {

    TextView fullName;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button bLogOutBtn,bNewAppealBtn,bpaymentBtn,bnewLeaseReqBtn;
    ImageButton bUserBtn;
    User user, userObj; //user is for getting data from the database, userObj is the object we get from last screen



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rented_home_page);


        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");


        fullName = findViewById(R.id.Name);
        bLogOutBtn = findViewById(R.id.logoutBtn);
        bUserBtn = findViewById(R.id.userBtn);
        bNewAppealBtn = findViewById(R.id.newAppealBtn);
        bpaymentBtn = findViewById(R.id.paymentBtn);
        bnewLeaseReqBtn = findViewById(R.id.newLeaseReqBtn);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null && value.exists()){
                    user = value.toObject(User.class);
                    fullName.setText(user.getFullName());          //extracting the user's full name from DB
                }
                else{
                    fullName.setText("User not found");
                }
            }
        });

        bpaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent RentedPaymentPage = new Intent(getApplicationContext(), RentedPaymentPage.class);
                RentedPaymentPage.putExtra("user", userObj); // Pass the User object to NewAppeal of renter
                startActivity(RentedPaymentPage);

            }
        });
        bLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(v);
            }
        });

        bUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userbtn = new Intent(getApplicationContext(), RentedHomePage.class);
                userbtn.putExtra("user", userObj);
                startActivity(userbtn);
            }
        });

        bNewAppealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newAppealIntent = new Intent(getApplicationContext(), NewAppealRented.class);
                newAppealIntent.putExtra("user", userObj); // Pass the User object to NewAppeal of renter
                startActivity(newAppealIntent);
            }
        });

        bnewLeaseReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newLeaseIntent = new Intent(getApplicationContext(), NewLeaseReq.class);
                newLeaseIntent.putExtra("user", userObj); // Pass the User object to NewLease of rented
                startActivity(newLeaseIntent);
            }
        });
    }

    public void logout(View view){
        fAuth.signOut(); //logout
        startActivity(new Intent(getApplicationContext(), Login.class));
    }
}