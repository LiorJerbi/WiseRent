package com.example.wiserent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Timer;
import java.util.TimerTask;

public class RentedHomePage extends AppCompatActivity {

    TextView fullName;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button bLogOutBtn,bNewAppealBtn,bpaymentBtn,bnewLeaseReqBtn,bTrackingAppealsBtn;
    ImageButton bUserBtn;
    User user, userObj; //user is for getting data from the database, userObj is the object we get from last screen

    Timer leaseCheckTimer;
    Handler handler;




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
        bTrackingAppealsBtn = findViewById(R.id.trackingAppealsBtn);

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

        bTrackingAppealsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AppealTrackRented = new Intent(getApplicationContext(),AppealTrackRented.class);
                AppealTrackRented.putExtra("user", userObj); // Pass the User object to AppealTrackRented of renter
                startActivity(AppealTrackRented);
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
                finish();
            }
        });


        bNewAppealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newAppealIntent = new Intent(getApplicationContext(), NewAppealRented.class);
                newAppealIntent.putExtra("user", userObj); // Pass the User object to NewAppeal of renter
                startActivity(newAppealIntent);
                finish();
            }
        });

        bnewLeaseReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newLeaseIntent = new Intent(getApplicationContext(), NewLeaseReq.class);
                newLeaseIntent.putExtra("user", userObj); // Pass the User object to NewLease of rented
                startActivity(newLeaseIntent);
                finish();
            }
        });


        // Initialize the handler for updating UI from background thread
        handler = new Handler(Looper.getMainLooper());

        // Start a timer to periodically check for lease updates
        startLeaseCheckTimer();
    }

    private void startLeaseCheckTimer() {
        leaseCheckTimer = new Timer();
        leaseCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Check for lease updates in the background
                checkForLeaseUpdates();
            }
        }, 0, 60000); // Check every 60 seconds (adjust the interval as needed)
    }

    private void checkForLeaseUpdates() {
        // Query lease collection for leases with the current rentedId
        fStore.collection("leases")
                .whereEqualTo("rentedId", userObj.getUserId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Handle the results of the query
                        for (DocumentSnapshot document : task.getResult()) {
                            Lease lease = document.toObject(Lease.class);

                            if (lease != null && lease.isStatus()) {
                                // Update the user's lease list
                                boolean check = true;
                                for(Lease property: userObj.getLeasedProperties()) {
                                    if (property.getLeaseId().equals(lease.getLeaseId())) {
                                        check = false;
                                    }
                                }
                                if(check){
                                    updateLeaseList(lease);
                                }
                            }
                        }
                    } else {
                        // Handle errors
                         Log.e("RentedHomePage", "Error getting leases: ", task.getException());
                    }
                });
    }

    private void updateLeaseList(Lease lease) {
        // Update the user's lease list in the UI thread
        handler.post(() -> {
            // Add the updated lease to the user's lease list
            userObj.addLease(lease);
            Toast.makeText(RentedHomePage.this,"חוזה אושר",Toast.LENGTH_LONG).show();
            fStore.collection("users")
                    .document(userObj.getUserId())
                    .set(userObj)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("RentedHomePage", "Add Successfully to User Collection ");
                        }
                    })
                    .addOnFailureListener(e ->{
                        Log.e("RentedHomePage","Fail to adding to User "+e.toString());
                    });

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the timer when the activity is destroyed
        if (leaseCheckTimer != null) {
            leaseCheckTimer.cancel();
        }
    }


    public void logout(View view){
        fAuth.signOut(); //logout
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}