package com.example.wiserent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    Button bLogoutBtn , bNewAppealBtn, bAssetCustomizeBtn;
    ImageButton bHomeBtn;
    User user, userObj; //user is for getting data from the database, userObj is the object we get from last screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_home_page);

        // Retrieve the User object passed from the previous activity
        Intent intent = getIntent();
        userObj = (User) intent.getSerializableExtra("user");

        fullName = findViewById(R.id.Name);
        bLogoutBtn = findViewById(R.id.logoutBtn);
        bHomeBtn = findViewById(R.id.homeBtn);
        bNewAppealBtn = findViewById(R.id.newAppealBtn);
        bAssetCustomizeBtn = findViewById(R.id.assetCustomizeBtn);

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

        bLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(v);
            }
        });

        bHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent renterIntent = new Intent(getApplicationContext(), RenterHomePage.class);
                renterIntent.putExtra("user", userObj); // Pass the User object to RentedHomePage
                startActivity(renterIntent);
            }
        });
        bNewAppealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newAppealIntent = new Intent(getApplicationContext(), NewAppealRenter.class);
                newAppealIntent.putExtra("user", userObj); // Pass the User object to NewAppeal of renter
                startActivity(newAppealIntent);
            }
        });

        bAssetCustomizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent assetCustomizeIntent = new Intent(getApplicationContext(), AssetCustomize.class);
                assetCustomizeIntent.putExtra("user", userObj); // Pass the User object to asset customize of renter
                startActivity(assetCustomizeIntent);
            }
        });
    }

    public void logout(View view){
        fAuth.signOut(); //logout
        startActivity(new Intent(getApplicationContext(), Login.class));
    }
}