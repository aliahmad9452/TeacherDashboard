package com.example.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView sign_up, Teacher_sign_up;
    private Button sign_in;
    private EditText email, pass;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sign_in = findViewById(R.id.sign_in);
        sign_up = findViewById(R.id.signup);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, proceed to home page
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        }


        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Sign_up.class);
                startActivity(intent);
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString().trim();
                String userPass = pass.getText().toString().trim();
                if (userEmail.isEmpty() || userPass.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(userEmail, userPass)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user != null) {
                                        // Sign-in success, proceed to home page
                                        startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                                        finish();
                                    }
                                } else {
                                    // Handle sign-in failures
                                    Toast.makeText(MainActivity.this, "Authentication failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
