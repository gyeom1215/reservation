package com.deu.reservation.server.model;

public class Room {
    public enum RoomType {
        LECTURE("일반강의실"),
        LAB("실습실");

        private final String displayName;

        RoomType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private String id;
    private String name;
    private int capacity;
    private String location;
    private boolean isAvailable;
    private RoomType roomType;

    public Room(String id, String name, int capacity, String location, RoomType roomType) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.isAvailable = true;
        this.roomType = roomType;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
} 