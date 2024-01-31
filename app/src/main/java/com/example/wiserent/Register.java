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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    //Creating variables for extracting info from the register screen
    EditText rFullName, rEmail, rPassword, rPhone;
    Button rRegisterBtn;
    TextView rLoginBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //extracting the data from the register screen to our variables
        rFullName = findViewById(R.id.FullName);
        rEmail = findViewById(R.id.Email);
        rPassword = findViewById(R.id.Password);
        rPhone = findViewById(R.id.phone);
        rRegisterBtn = findViewById(R.id.RegisterBtn);
        rLoginBtn = findViewById(R.id.createText);
        progressBar = findViewById(R.id.progressBar);

        //Authentication instance to register our new user to firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        //checking if there is a user already connected so register not necessary
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), PremissionType.class));
            finish();
        }

        rRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = rEmail.getText().toString().trim();
                String password = rPassword.getText().toString().trim();
                String fullName = rFullName.getText().toString().trim();
                String phone = rPhone.getText().toString().trim();

                //checking for null email or password
                if(TextUtils.isEmpty(email)){
                    rEmail.setError("שדה אימייל ריק.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    rPassword.setError("שדה סיסמא ריק.");
                    return;
                }

                //checking for short password
                if(password.length() < 6){
                    rPassword.setError("סיסמא קצרה מדי צריכה להכיל לפחות 6 תווים.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //register the user in firebase
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "המשתמש נוצר.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fullname",fullName);
                            user.put("email",email);
                            user.put("phone",phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG","onSuccess: user Profile is created for "+userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG","onFailiure: "+e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), PremissionType.class));
                        }
                        else{
                            Toast.makeText(Register.this,"שגיאה!" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        rLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}