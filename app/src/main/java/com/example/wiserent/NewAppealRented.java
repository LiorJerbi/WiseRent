package com.example.wiserent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewAppealRented extends AppCompatActivity {
    EditText appealEditText;
    ImageButton userBtn;
    Button sendAppealBtn;
    User userObj;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appeal_rented);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");

        userBtn = findViewById(R.id.userBtn);
        sendAppealBtn = findViewById(R.id.sendAppealBtn);
        appealEditText = findViewById(R.id.appealEditText);
        fStore = FirebaseFirestore.getInstance();

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userbtn = new Intent(getApplicationContext(), RentedHomePage.class);
                userbtn.putExtra("user", userObj);
                startActivity(userbtn);
            }
        });

        sendAppealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve appeal text from EditText
                String appealText = appealEditText.getText().toString();

                // Create a new GeneralProblem appeal
                GeneralProblem generalProblem = new GeneralProblem(appealText, false);

                // Save the GeneralProblem appeal to Firebase
                saveGeneralProblemToFirebase(generalProblem, userObj);
            }
        });
    }

    private void saveGeneralProblemToFirebase(GeneralProblem generalProblem, User userObj) {
        // Save the GeneralProblem appeal to the appeals collection
        DocumentReference appealReference = fStore.collection("appeals").document();

        // Set the appealId to the generated document ID
        generalProblem.setAppealId(appealReference.getId());

        // Add the GeneralProblem to the user's local list of appeals
        userObj.addAppeal(generalProblem);

        // Save the GeneralProblem to the appeals collection
        appealReference.set(generalProblem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(NewAppealRented.this, "התלונה נשלחה בהצלחה!", Toast.LENGTH_SHORT).show();
                        // Update the user's appeal list in the user document
                        updateAppealListInUserDocument(userObj);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(NewAppealRented.this, "שגיאה: " + e.toString(), Toast.LENGTH_SHORT).show();
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
                        finish(); // Close the activity after successful update
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure to update user appeal list
                        Toast.makeText(NewAppealRented.this, "Failed to update user appeal list", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
