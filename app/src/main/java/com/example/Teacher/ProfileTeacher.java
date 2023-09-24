package com.example.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileTeacher extends AppCompatActivity {
    private ImageView profilePic;
    private EditText usernameInput;
    private EditText phoneInput;
    private Button updateProfileBtn;
    private ProgressBar progressBar;
    private TextView logoutBtn;
    private EditText userClass;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseUser currentUser;
    private Uri selectedImageUri;

    private static final int GALLERY_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_teacher);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize UI elements
        profilePic = findViewById(R.id.profile_image_view);
        usernameInput = findViewById(R.id.profile_username);
        phoneInput = findViewById(R.id.profile_phone);
        updateProfileBtn = findViewById(R.id.profle_update_btn);
        progressBar = findViewById(R.id.profile_progress_bar);
        logoutBtn = findViewById(R.id.logout_btn);
        userClass = findViewById(R.id.userClass);

        // Set a click listener for the profile picture to open the gallery
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Retrieve the user details from Firestore and update the views
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            firestore.collection("teachers").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    String className = document.getString("Class");
                                    String teacherName = document.getString("Name");
                                    String password = document.getString("Password");
                                    String email = document.getString("email");
                                    boolean firstLogin = document.getBoolean("firstLogin");

                                    // Update your views with the retrieved data
                                    usernameInput.setText(teacherName);
                                    phoneInput.setText(email);
                                    userClass.setText(className);



                                    // Load and display the profile picture if available
                                    String profileImageUrl = document.getString("profileImageUrl");
                                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                        // Using Glide to load the profile image into the ImageView
                                        Glide.with(ProfileTeacher.this)
                                                .load(profileImageUrl)
                                                .into(profilePic);
                                    }
                                }
                            }
                        }
                    });
        }

        // Set a click listener for the "Update Profile" button
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Set a click listener for the "Logout" button in the activity
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Perform the logout action
                            // You can replace the following code with your actual logout logic
                            FirebaseAuth.getInstance().signOut(); // Sign out the user
                            Intent intent = new Intent(ProfileTeacher.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }
}
