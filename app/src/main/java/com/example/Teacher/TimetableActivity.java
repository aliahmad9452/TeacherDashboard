package com.example.Teacher;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Teacher.ModelClass.TimeTableEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Locale;

public class TimetableActivity extends AppCompatActivity {

    private EditText lectureDetailsEditText;
    private EditText timeEditText;
    private TextView textViewTeacherName;
    private Button createButton,buttonSelectTime;
    Spinner semesterItems, classSpinner;

    private CollectionReference timetableRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        timetableRef = firestore.collection("timetable");

        lectureDetailsEditText = findViewById(R.id.editTextLectureDetails);
        textViewTeacherName = findViewById(R.id.textViewTeacherName);
        createButton = findViewById(R.id.btnCreate);
        classSpinner = findViewById(R.id.classSpinner);
        semesterItems = findViewById(R.id.semesterItems);
        // Assuming you have a FirebaseUser object representing the logged-in teacher
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String teacherUid = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference teacherRef = db.collection("teachers").document(teacherUid);

            teacherRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String teacherName = documentSnapshot.getString("Name");
                    textViewTeacherName.setText(teacherName);
                }
            }).addOnFailureListener(e -> {
                // Handle the failure to retrieve the teacher's name from Firestore
            });
        }


        buttonSelectTime = findViewById(R.id.buttonSelectTime);
        buttonSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.classes_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(adapter);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem().toString().contains("MS")) {
                    ArrayAdapter<String> adapterSemester = new ArrayAdapter<>(TimetableActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.semester4_array));
                    adapterSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    semesterItems.setAdapter(adapterSemester);
                } else {
                    ArrayAdapter<String> adapterSemester = new ArrayAdapter<>(TimetableActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.semester8_array));
                    adapterSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    semesterItems.setAdapter(adapterSemester);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimetableEntry();
            }
        });
    }
        private void showTimePickerDialog () {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            // Handle the selected time (hourOfDay and minute)
                            String amPm;
                            int hour;

                            if (hourOfDay >= 12) {
                                amPm = "PM";
                                if (hourOfDay > 12) {
                                    hour = hourOfDay - 12;
                                } else {
                                    hour = hourOfDay;
                                }
                            } else {
                                amPm = "AM";
                                if (hourOfDay == 0) {
                                    hour = 12;
                                } else {
                                    hour = hourOfDay;
                                }
                            }

                            String selectedTime = String.format(Locale.US, "%02d:%02d %s", hour, minute, amPm);
                            // Update the UI or save the selected time as needed
                            // For example, if you have a TextView for displaying the selected time:
                             buttonSelectTime.setText(selectedTime);


                        }
                    },
                    // Set the initial time (optional)
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().get(Calendar.MINUTE),
                    false // 12-hour format with AM/PM
            );

            timePickerDialog.show();
        }


    private void createTimetableEntry() {
        String lectureDetails = lectureDetailsEditText.getText().toString();
        String time = buttonSelectTime.getText().toString();
        String teacherName = textViewTeacherName.getText().toString();
        String userClass = classSpinner.getSelectedItem().toString(); // Get the selected class from the spinner
        String userSemester = semesterItems.getSelectedItem().toString();

        // Create a new TimeTableEntry instance with the data
        TimeTableEntry newEntry = new TimeTableEntry(lectureDetails, time, teacherName, userClass, userSemester);

        // Add the new entry to Firestore
        timetableRef.add(newEntry)
                .addOnSuccessListener(documentReference -> {
                    // Show a toast indicating success
                    Toast.makeText(TimetableActivity.this, "New timetable entry created successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Show a toast indicating failure
                    Toast.makeText(TimetableActivity.this, "Failed to create new timetable entry", Toast.LENGTH_SHORT).show();
                });
    }
}
