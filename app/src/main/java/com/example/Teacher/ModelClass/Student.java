package com.example.Teacher.ModelClass;

public class Student {
    private String studentId;
    private String studentName;
    private String userClass;
    private String userSemester;
    // Other properties, getters, and setters as needed

    public Student() {
        // Default constructor required for Firebase
    }

    public Student(String studentId, String studentName, String userClass, String userSemester) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.userClass = userClass;
        this.userSemester = userSemester;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getUserClass() {
        return userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }

    public String getUserSemester() {
        return userSemester;
    }

    public void setUserSemester(String userSemester) {
        this.userSemester = userSemester;
    }
}
