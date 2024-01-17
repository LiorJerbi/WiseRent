package com.example.wiserent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Register extends AppCompatActivity {
    //Creating variables for extracting info from the register screen
    EditText rFullName, rEmail, rPassword, rPhone;
    Button rRegisterBtn;
    TextView rLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

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
        //checking if there is a user already connected so register not necessary
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        rRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = rEmail.getText().toString().trim();
                String password = rPassword.getText().toString().trim();

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
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
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