package model.room;

import java.util.List;

public class RoomReservation {
    private Integer roomNumber;
    private String day;       // 요일
    private List<String> timeSlots;
    private String roomType;
    private String state;     // "대기" 등

    public RoomReservation(Integer roomNumber, String day, List<String> timeSlots, String roomType, String state, String name, String role) {
        this.roomNumber = roomNumber;
        this.day = day;
        this.timeSlots = timeSlots;
        this.roomType = roomType;
        this.state = state;
    }
    public Integer getRoomNumber() {
        return roomNumber;
    }

    public String getDay() {
        return day;
    }

    public List<String> getTimeSlots() {
        return timeSlots;
    }

}
