package com.example.Teacher.ModelClass;

public class TimeTableEntry {
    private String lectureDetails;
    private String time;
    private String teacherName;
    private String userClass;
    private String userSemester;

    public String getUserSemester() {
        return userSemester;
    }

    public void setUserSemester(String userSemester) {
        this.userSemester = userSemester;
    }


    public TimeTableEntry() {
        // Default constructor required for Firebase
    }

    public String getUserClass() {
        return userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }

    public TimeTableEntry(String lectureDetails, String time, String teacherName, String userClass,String userSemester) {
        this.userSemester = userSemester;
        this.userClass = userClass;
        this.lectureDetails = lectureDetails;
        this.time = time;
        this.teacherName = teacherName;
    }

    public String getLectureDetails() {
        return lectureDetails;
    }

    public void setLectureDetails(String lectureDetails) {
        this.lectureDetails = lectureDetails;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public java.util.Map<String, Object> toMap() {
        java.util.HashMap<String, Object> result = new java.util.HashMap<>();
        result.put("lectureDetails", lectureDetails);
        result.put("time", time);
        result.put("teacherName", teacherName);
        result.put("User Class","" );
        result.put("userSemester","");
        return result;
    }
}
