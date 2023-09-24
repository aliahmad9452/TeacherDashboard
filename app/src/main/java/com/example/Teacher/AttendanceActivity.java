package com.example.Teacher;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity {

    private Spinner classSpinner, semesterSpinner;
    private Button loadStudentsButton;
    private LinearLayout studentListLayout;
    private CollectionReference studentsCollection;
    private CollectionReference attendanceCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        classSpinner = findViewById(R.id.classSpinner);
        semesterSpinner = findViewById(R.id.semesterItems);
        loadStudentsButton = findViewById(R.id.loadStudentsButton);
        studentListLayout = findViewById(R.id.studentListLayout);

        // Initialize Firebase Firestore references
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        studentsCollection = firestore.collection("users");
        attendanceCollection = firestore.collection("attendance");

        // Populate class spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.classes_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(adapter);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem().toString().contains("MS")) {
                    ArrayAdapter<String> adapterSemester = new ArrayAdapter<>(AttendanceActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.semester4_array));
                    adapterSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    semesterSpinner.setAdapter(adapterSemester);
                } else {
                    ArrayAdapter<String> adapterSemester = new ArrayAdapter<>(AttendanceActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.semester8_array));
                    adapterSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    semesterSpinner.setAdapter(adapterSemester);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadStudentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadStudents();
            }
        });
    }

    private void loadStudents() {
        studentListLayout.removeAllViews();
        String selectedClass = classSpinner.getSelectedItem().toString();
        String selectedSemester = semesterSpinner.getSelectedItem().toString();

        // Query Firestore to filter students by class and semester
        Query studentsQuery = studentsCollection
                .whereEqualTo("userClass", selectedClass)
                .whereEqualTo("userSemester", selectedSemester);

        studentsQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot studentSnapshot : queryDocumentSnapshots) {
                String studentName = studentSnapshot.getString("username");
                String rollNumber = studentSnapshot.getString("rollNumber");

                if (studentName != null) {
                    // Create a custom layout for each student entry
                    LinearLayout studentEntryLayout = new LinearLayout(AttendanceActivity.this);
                    studentEntryLayout.setOrientation(LinearLayout.HORIZONTAL);

                    // Create a CheckBox for attendance
                    CheckBox checkBox = new CheckBox(AttendanceActivity.this);
                    checkBox.setText("Name: " + studentName);

                    // Create TextView for roll number
                    TextView rollNumberTextView = new TextView(AttendanceActivity.this);
                    rollNumberTextView.setText("           Roll Number: " + rollNumber);

                    // Add CheckBox and TextView to the student entry layout
                    studentEntryLayout.addView(checkBox);
                    studentEntryLayout.addView(rollNumberTextView);

                    // Attach a click listener to mark attendance
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String attendanceStatus = ((CheckBox) v).isChecked() ? "Present" : "Absent";
                            String studentName = studentSnapshot.getString("username"); // Get the student's name
                            String studentClass = selectedClass; // Get the selected class
                            String studentSemester = selectedSemester; // Get the selected semester

                            // Call markAttendance with additional parameters
                            markAttendance(studentSnapshot.getId(), studentName, studentClass, studentSemester, attendanceStatus);
                        }
                    });


                    // Set CheckBox color based on attendance status
                    setCheckBoxColor(checkBox, studentSnapshot.getId(), selectedClass, selectedSemester);

                    // Add the student entry layout to the main layout
                    studentListLayout.addView(studentEntryLayout);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(AttendanceActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show();
        });
    }

    private void markAttendance(String studentId, String studentName, String studentClass, String studentSemester, String status) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        // Create a map to store attendance data
        Map<String, Object> attendanceData = new HashMap<>();
        attendanceData.put("date", formattedDate);
        attendanceData.put("status", status);
        attendanceData.put("studentName", studentName);
        attendanceData.put("userClass", studentClass);
        attendanceData.put("userSemester", studentSemester);

        // Update attendance data in Firestore
        attendanceCollection
                .document(studentId)
                .collection("attendance")
                .add(attendanceData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AttendanceActivity.this, "Attendance marked: " + status, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AttendanceActivity.this, "Failed to mark attendance", Toast.LENGTH_SHORT).show();
                });
    }


    private void setCheckBoxColor(CheckBox checkBox, String studentId, String studentClass, String studentSemester) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        // Query Firestore to get attendance status
        attendanceCollection
                .document(studentId)
                .collection("attendance")
                .document(formattedDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String attendanceStatus = documentSnapshot.getString("status");
                        if (attendanceStatus != null) {
                            if (attendanceStatus.equals("Present")) {
                                checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
                            } else if (attendanceStatus.equals("Absent")) {
                                checkBox.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to load attendance status
                });
    }
}
