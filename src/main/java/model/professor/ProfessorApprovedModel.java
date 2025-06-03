package model.professor;

import java.util.List;

public class ProfessorApprovedModel {
    private String role;
    private int roomNumber;
    private String name;
    private List<String> timeSlots;
    private String state;
    private String day;
    private String roomType;

    // Jackson용 기본 생성자 추가
    public ProfessorApprovedModel() {
    }

    public ProfessorApprovedModel(String role, int roomNumber, String name, List<String> timeSlots, String state, String day, String roomType) {
        this.role = role;
        this.roomNumber = roomNumber;
        this.name = name;
        this.timeSlots = timeSlots;
        this.state = state;
        this.day = day;
        this.roomType = roomType;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getTimeSlots() {return timeSlots;}
    public void setTimeSlots(List<String> timeSlots) { this.timeSlots = timeSlots; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
}