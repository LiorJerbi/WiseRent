package com.example.wiserent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private List<Property> properties;
    private PropertyClickListener propertyClickListener;

    public PropertyAdapter(List<Property> properties, PropertyClickListener propertyClickListener) {
        this.properties = properties;
        this.propertyClickListener = propertyClickListener;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.bind(property);
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public void updateData(List<Property> properties) {
        this.properties = properties;
        notifyDataSetChanged();
    }

    public class PropertyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvPropertyAddress;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPropertyAddress = itemView.findViewById(R.id.tvPropertyAddress);

            // Set click listener on the entire item view
            itemView.setOnClickListener(this);
        }

        public void bind(Property property) {
            tvPropertyAddress.setText(property.getAddress());
        }

        @Override
        public void onClick(View view) {
            // Delegate click handling to the interface method
            propertyClickListener.onPropertyClick(properties.get(getAdapterPosition()).getPropertyId());
        }
    }

    // Interface for handling property clicks
    public interface PropertyClickListener {
        void onPropertyClick(String propertyId);
    }
}