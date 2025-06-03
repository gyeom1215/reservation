package model.common;

import java.util.List;

public class Reservation {
    private String name;
    private String role;
    private String roomType;
    private int roomNumber;
    private String day;
    private List<String> timeSlots;
    private String state;

    public Reservation() {
        // Default constructor for JSON serialization
    }

    public Reservation(String name, String role, String roomType, int roomNumber, String day, List<String> timeSlots, String state) {
        this.name = name;
        this.role = role;
        this.roomType = roomType;
        this.roomNumber = roomNumber;
        this.day = day;
        this.timeSlots = timeSlots;
        this.state = state;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    
    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }
    
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    
    public List<String> getTimeSlots() { return timeSlots; }
    public void setTimeSlots(List<String> timeSlots) { this.timeSlots = timeSlots; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
} 