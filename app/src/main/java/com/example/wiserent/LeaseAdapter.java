package com.example.wiserent;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LeaseAdapter extends ArrayAdapter<Lease> {

    FirebaseFirestore fStore;
    public LeaseAdapter(Context context, List<Lease> leases, FirebaseFirestore fStore) {
        super(context, 0, leases);
        this.fStore = fStore;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = new TextView(getContext());
        Lease lease = getItem(position);
        if (lease != null) {
            // Display the property address in the Spinner
            getPropertyAddress(lease.getPropertyId(), textView);
        }
        return textView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = new TextView(getContext());
        Lease lease = getItem(position);
        if (lease != null) {
            // Display the property address in the dropdown
            getPropertyAddress(lease.getPropertyId(), textView);
        }
        return textView;
    }

    private void getPropertyAddress(String propertyId, final TextView textView) {
        if (propertyId != null) {
            // Fetch property details from Firestore based on propertyId
            DocumentReference propertyRef = fStore.collection("properties").document(propertyId);
            propertyRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Property property = documentSnapshot.toObject(Property.class);
                        if (property != null) {
                            textView.setText(property.getAddress());
                        }
                    }
                }
            });
        }
    }
}

