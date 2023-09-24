package com.example.Teacher;

public class Teacher {
    private String teacherId;
    private String fullName;
    private String email;

    public Teacher() {
        // Empty constructor required for Firebase
    }

    public Teacher(String teacherId, String fullName, String email) {
        this.teacherId = teacherId;
        this.fullName = fullName;
        this.email = email;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
}