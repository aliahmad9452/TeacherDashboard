package com.example.Teacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class Sign_up extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, passwordEditText;
    private Button signUpButton;
    private TextView signInTextView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    Spinner classSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        fullNameEditText = findViewById(R.id.fullNameEditText);
        classSpinner = findViewById(R.id.classSpinner);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        signInTextView = findViewById(R.id.signin);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.classes_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(adapter);

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no item selected if needed
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpTeacher();
            }
        });

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the sign-in activity or fragment here
                // For example, you can use Intent to navigate to the sign-in activity
                Intent intent = new Intent(Sign_up.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }

    private void signUpTeacher() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String userClass = classSpinner.getSelectedItem().toString();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || userClass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign up the teacher with email and password
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Teacher sign up successful, save additional details to Firestore
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();

                            // Set the firstLogin flag to true for new users
                            boolean isFirstLogin = true;

                            // Create a Map to store user data
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("Name", fullName);
                            userData.put("email", email);
                            userData.put("Password", password);
                            userData.put("Class", userClass);
                            userData.put("firstLogin", isFirstLogin);

                            firestore.collection("teachers")
                                    .document(userId)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Teacher sign up successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Sign_up.this, MainActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to save teacher details to Firestore", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Handle sign up failures
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthUserCollisionException e) {
                            Toast.makeText(this, "Email is already registered. Please sign in.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(this, "Sign up failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
