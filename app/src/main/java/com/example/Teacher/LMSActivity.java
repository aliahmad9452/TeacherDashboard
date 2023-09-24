package com.example.Teacher;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Teacher.ModelClass.AssignmentModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LMSActivity extends AppCompatActivity {

    Spinner semesterItems, classSpinner;
    ProgressBar progressbar;

    private EditText editTextTitle, editTextDescription, editTextDueDate;
    private Button createButton, uploadPdfButton;
    private static final int REQUEST_PDF_PICKER = 1; // Define a request code constant
    private Button buttonSelectDueDate;
    private Calendar calendar;

    private CollectionReference assignmentsCollection;
    private StorageReference storageRef;
    TextView textViewTeacherName;
    private Uri selectedPdfUri = null; // Store the selected PDF URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmsactivity);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        assignmentsCollection = firestore.collection("assignments");

        uploadPdfButton = findViewById(R.id.uploadPdfButton);
        progressbar = findViewById(R.id.progressbar);
        classSpinner = findViewById(R.id.classSpinner);
        semesterItems = findViewById(R.id.semesterItems);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        createButton = findViewById(R.id.btnCreate);
        textViewTeacherName = findViewById(R.id.textViewTeacherName);

        storageRef = FirebaseStorage.getInstance().getReference(); // Initialize storage reference
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
        uploadPdfButton = findViewById(R.id.uploadPdfButton);
        uploadPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.setVisibility(View.VISIBLE);
                // Open file picker or implement file upload logic
                // Example: Call a method to handle file upload
                openFilePicker();
            }
        });
        buttonSelectDueDate = findViewById(R.id.buttonSelectDueDate);
        calendar = Calendar.getInstance();

        buttonSelectDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.classes_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(adapter);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem().toString().contains("MS")) {
                    ArrayAdapter<String> adapterSemester = new ArrayAdapter<>(LMSActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.semester4_array));
                    adapterSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    semesterItems.setAdapter(adapterSemester);
                } else {
                    ArrayAdapter<String> adapterSemester = new ArrayAdapter<>(LMSActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.semester8_array));
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
                createAssignmentEntry();
            }
        });
    }
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Get the current date
                        Calendar currentDate = Calendar.getInstance();

                        // Set the selected date
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Check if the selected date is not in the past
                        if (calendar.compareTo(currentDate) >= 0) {
                            // Format the selected date and set it as the button text
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                            String selectedDate = dateFormat.format(calendar.getTime());
                            buttonSelectDueDate.setText(selectedDate);
                        } else {
                            // Display an error message if the selected date is in the past
                            Toast.makeText(LMSActivity.this, "Please select a future date.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set the minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    private void openFilePicker() {
        // Implement your file picker logic here
        // Once the file is picked, upload it to Firebase Storage
        // Here's a simplified example using startActivityForResult:
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_PDF_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PDF_PICKER && resultCode == RESULT_OK && data != null) {
            selectedPdfUri = data.getData();
            if (selectedPdfUri != null) {
                // Upload the selected PDF file to Firebase Storage
                StorageReference pdfRef = storageRef.child("pdfs").child(selectedPdfUri.getLastPathSegment());

                pdfRef.putFile(selectedPdfUri)
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                // Update the progress if needed
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressbar.setVisibility(View.GONE);

                                // Calculate the upload progress in percentage
                                // File uploaded successfully
                                // Get the download URL of the uploaded PDF
                                pdfRef.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String pdfDownloadUrl = uri.toString();

                                                // Create a new assignment with the PDF URL
                                                String title = editTextTitle.getText().toString();
                                                String description = editTextDescription.getText().toString();
                                                String dueDate = buttonSelectDueDate.getText().toString();
                                                String teacherName = textViewTeacherName.getText().toString();
                                                String userClass = classSpinner.getSelectedItem().toString();
                                                String userSemester = semesterItems.getSelectedItem().toString();

                                                saveAssignmentData(title,teacherName, description, dueDate, userClass, userSemester, pdfDownloadUrl);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Handle getting the download URL failure
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle file upload failure
                            }
                        });
            }
        }
    }

    private void createAssignmentEntry() {
        // Check if a PDF was selected
        if (selectedPdfUri != null) {
            // A PDF was selected, proceed with uploading and saving the assignment
            progressbar.setVisibility(View.VISIBLE);
            openFilePicker(); // Trigger PDF upload
        } else {
            // No PDF was selected, save assignment data without a PDF URL
            String title = editTextTitle.getText().toString();
            String description = editTextDescription.getText().toString();
            String dueDate = buttonSelectDueDate.getText().toString();
            String teacherName = textViewTeacherName.getText().toString();
            String userClass = classSpinner.getSelectedItem().toString();
            String userSemester = semesterItems.getSelectedItem().toString();

            saveAssignmentData(title,teacherName, description, dueDate, userClass, userSemester, "");
        }
    }

    private void saveAssignmentData(String title,String teacherName,String description, String dueDate, String userClass, String userSemester, String pdfDownloadUrl) {
        AssignmentModel newAssignment = new AssignmentModel(title,teacherName, description, dueDate, userClass, userSemester, pdfDownloadUrl);

        // Add the new assignment to Firestore
        assignmentsCollection.add(newAssignment)
                .addOnSuccessListener(documentReference -> {
                    // Show a toast indicating success
                    Toast.makeText(LMSActivity.this, "New assignment created successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Show a toast indicating failure
                    Toast.makeText(LMSActivity.this, "Failed to create new assignment", Toast.LENGTH_SHORT).show();
                });
    }
}
