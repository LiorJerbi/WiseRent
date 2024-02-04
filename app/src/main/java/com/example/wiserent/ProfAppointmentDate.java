package com.example.wiserent;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfAppointmentDate extends AppCompatActivity {

    ImageButton homeBtn;
    Button selectDateButton, submitButton;
    Spinner professionalTypeSpinner;
    EditText selectedDateEditText;
    ProfessionalAppointment professionalAppointment;
    User userObj;
    Property selectedProperty;
    FirebaseFirestore fStore;
    Calendar selectedCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_appointment_date);

        // Retrieve the User object and selected property passed from the previous activity
        userObj = (User) getIntent().getSerializableExtra("user");
        selectedProperty = (Property) getIntent().getSerializableExtra("selectedProperty");

        fStore = FirebaseFirestore.getInstance();
        professionalAppointment = new ProfessionalAppointment();

        homeBtn = findViewById(R.id.homeBtn);
        selectDateButton = findViewById(R.id.selectDateButton);
        submitButton = findViewById(R.id.submitButton);
        professionalTypeSpinner = findViewById(R.id.professionalTypeSpinner);
        selectedDateEditText = findViewById(R.id.selectedDateEditText);

        // Setup spinner with professional types
        ArrayAdapter<CharSequence> professionalTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.professional_types_array, android.R.layout.simple_spinner_item);
        professionalTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        professionalTypeSpinner.setAdapter(professionalTypeAdapter);

        // Initialize Calendar for date selection
        selectedCalendar = Calendar.getInstance();

        // Set onClickListener for the date selection button
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set onClickListener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProfessionalAppointment();
                Intent newApprenterIntent = new Intent(getApplicationContext(), NewAppealRenter.class);
                newApprenterIntent.putExtra("user", userObj); // Pass the User object to New appeal renter
                startActivity(newApprenterIntent);
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent renterIntent = new Intent(getApplicationContext(), RenterHomePage.class);
                renterIntent.putExtra("user", userObj); // Pass the User object to RentedHomePage
                startActivity(renterIntent);
            }
        });

    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedCalendar.set(Calendar.YEAR, year);
                selectedCalendar.set(Calendar.MONTH, monthOfYear);
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateSelectedDate();
            }
        };

        new DatePickerDialog(this, dateSetListener,
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateSelectedDate() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        selectedDateEditText.setText(sdf.format(selectedCalendar.getTime()));
    }

    private void submitProfessionalAppointment() {
        String professionalType = professionalTypeSpinner.getSelectedItem().toString().trim();
        String selectedDate = selectedDateEditText.getText().toString().trim();

        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Select a date for the appointment", Toast.LENGTH_SHORT).show();
            return;
        }

        professionalAppointment.setProfessionalType(professionalType);
        professionalAppointment.setDate(selectedDate);
        professionalAppointment.setStatus(false); // Assuming status is initially set to false

        saveProfessionalAppointmentToFirebase(professionalAppointment, selectedProperty);
    }

    private void saveProfessionalAppointmentToFirebase(ProfessionalAppointment profAppointment, Property selectedProperty) {
        // Save the ProfessionalAppointment to the appeals collection
        DocumentReference appealReference = fStore.collection("appeals").document();
        // Set the appealId to the generated document ID
        profAppointment.setAppealId(appealReference.getId());
        // Set the propertyId in the appeal
        profAppointment.setPropertyID(selectedProperty.getPropertyId());
        // Add the Bill to the user's local list of appeals
        userObj.addAppeal(profAppointment);

        // Save the Bill to the appeals collection
        appealReference.set(profAppointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ProfAppointmentDate.this, "פגישה עם בעל מקצוע תואמה בהצלחה!", Toast.LENGTH_SHORT).show();
                        Log.d("ProfAppointmentDate", "Appointment stored in separate collection, user appeal list updated locally and in the database");
                        // Update the user's appeal list in the user document
                        updateAppealListInUserDocument(userObj);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfAppointmentDate.this, "שגיאה: " + e.toString(), Toast.LENGTH_SHORT).show();
                        Log.e("ProfAppointmentDate", "Error saving appointment to Firebase: " + e.toString());
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
                        Log.d("ProfAppointmentDate", "User appeal list updated successfully in the database");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ProfAppointmentDate", "Failed to update user appeal list in the database", e);
                    }
                });
    }
}