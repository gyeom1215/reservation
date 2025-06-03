package model.student;

import java.time.LocalDateTime;

public class StudentReservation {
    private String roomNumber;
    private String roomType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private String studentId;

    public StudentReservation(String roomNumber, String roomType, LocalDateTime startTime, 
                            LocalDateTime endTime, String purpose, String studentId) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.studentId = studentId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStudentId() {
        return studentId;
    }
} 