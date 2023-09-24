package com.example.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class DashboardActivity extends AppCompatActivity {
    private static final int PROFILE_ACTIVITY_REQUEST_CODE = 1;
    FirebaseAuth firebaseAuth;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button attendanceButton = findViewById(R.id.Attendance);

        handleTeacherLogin();


        attendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAttendanceActivity();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Subscribe to a topic
        FirebaseMessaging.getInstance().subscribeToTopic("Teacher")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String message = "done";
                        if (!task.isSuccessful()) {
                            message = "Failed";
                        }
                    }
                });

        profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ProfileTeacher.class);
                startActivity(intent);
                startActivityForResult(intent, PROFILE_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    public void openTimetable(View view) {
        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
    }

    public void openLMS(View view) {
        Intent intent = new Intent(this, LMSActivity.class);
        startActivity(intent);
    }

    public void openAttendanceActivity() {
        Intent intent = new Intent(this, AttendanceActivity.class);
        startActivity(intent);
    }

    public void openUpdates(View view) {
        Intent intent = new Intent(this, updates.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update the profile picture in the HomePage
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(DashboardActivity.this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.baseline_face_24) // Placeholder image while loading
                                .error(R.drawable.baseline_face_24) // Image to display if loading fails
                                .into(profileImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error if needed
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROFILE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Profile activity returned successfully
            // Check if the profile image URL is available in the data
            if (data != null && data.hasExtra("profileImageUrl")) {
                // Get the profile image URL from the intent data
                String profileImageUrl = data.getStringExtra("profileImageUrl");

                // Load the updated profile image into the circular ImageView using Glide
                Glide.with(this)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.baseline_face_24) // Placeholder image while loading
                        .error(R.drawable.baseline_face_24) // Image to display if loading fails
                        .into(profileImage);
            }
        }
    }
    private void handleTeacherLogin() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String teacherUid = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference teacherRef = db.collection("teachers").document(teacherUid);

            teacherRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Check if it's the first login
                    boolean isFirstLogin = documentSnapshot.getBoolean("firstLogin");

                    if (isFirstLogin) {
                        // Perform actions for the first login
                        // For example, show a welcome message or perform setup tasks

                        // Update the "firstLogin" field to indicate that the teacher has logged in
                        teacherRef.update("firstLogin", false)
                                .addOnSuccessListener(aVoid -> {
                                    // Log the teacher in for the first time
                                    // ...
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure to update "firstLogin" if needed
                                });
                    } else {
                        // This is not the first login
                        // Proceed with the regular login flow
                        // ...
                    }
                }
            }).addOnFailureListener(e -> {
                // Handle the failure to retrieve teacher data from Firestore
            });
        }
    }
}

