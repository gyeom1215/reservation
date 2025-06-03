/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.ta;
import java.util.List;

public class Reservation {
    private String name;
    private String role;
    private String type;              // roomType
    private int roomNumber;
    private String day;
    private List<String> timeSlots;
    private String state;

    // 생성자
    public Reservation(String name, String role, String type, int roomNumber, String day, List<String> timeSlots, String state) {
        this.name = name;
        this.role = role;
        this.type = type;
        this.roomNumber = roomNumber;
        this.day = day;
        this.timeSlots = timeSlots;
        this.state = state;
    }

    // Getter들
    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getType() {
        return type;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getDay() {
        return day;
    }

    public List<String> getTimeSlots() {
        return timeSlots;
    }

    public String getState() {
        return state;
    }
}
