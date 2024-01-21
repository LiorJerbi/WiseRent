package com.example.wiserent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class RenterHomePage extends AppCompatActivity {

    TextView fullName;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    Button bLogoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_home_page);

        fullName = findViewById(R.id.Name);
        bLogoutBtn = findViewById(R.id.logoutBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                fullName.setText(value.getString("fullname"));          //extracting the user's full name from DB
            }
        });

        bLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(v);
            }
        });

    }

    public void logout(View view){
        fAuth.signOut(); //logout
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}