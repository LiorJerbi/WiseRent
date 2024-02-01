package com.example.wiserent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText lEmail,lPassword;
    Button lLoginBtn;
    TextView lRegisterBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lEmail = findViewById(R.id.Email);
        lPassword = findViewById(R.id.Password);
        lLoginBtn = findViewById(R.id.LoginBtn);
        lRegisterBtn = findViewById(R.id.createText);
        progressBar = findViewById(R.id.progressBar2);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        lLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = lEmail.getText().toString().trim();
                String password = lPassword.getText().toString().trim();

                //checking for null email or password
                if(TextUtils.isEmpty(email)){
                    lEmail.setError("שדה אימייל ריק.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    lPassword.setError("שדה סיסמא ריק.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Authenticate the user in Firebase
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "התחברת בהצלחה.", Toast.LENGTH_SHORT).show();

                            // Retrieve the User object from Firestore
                            String userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            User user = document.toObject(User.class);

                                            // Pass the User object to the next activity
                                            Intent intent = new Intent(getApplicationContext(), PremissionType.class);
                                            intent.putExtra("user", user);
                                            startActivity(intent);
                                        }
                                    } else {
                                        // Handle the error
                                        Toast.makeText(Login.this, "שגיאה!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);

                                        // Log the error for debugging purposes
                                        Log.e("LoginActivity", "Error signing in", task.getException());
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Login.this, "שגיאה!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        lRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}