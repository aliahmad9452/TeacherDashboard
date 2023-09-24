package com.example.Teacher.ModelClass;

public class AttendanceModel {
    private String userId;
    private boolean isPresent;

    public AttendanceModel() {
        // Empty constructor required for Firebase
    }

    public AttendanceModel(String userId, boolean isPresent) {
        this.userId = userId;
        this.isPresent = isPresent;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isPresent() {
        return isPresent;
    }
}
