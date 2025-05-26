package com.deu.reservation.client.model;

public class Room {
    private String id;
    private String name;
    private int capacity;
    private String location;
    private boolean hasProjector;
    private boolean hasComputer;
    private String type; // "일반강의실" 또는 "실습실"

    public Room(String id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public Room(String id, String name, int capacity, String location, 
               boolean hasProjector, boolean hasComputer, String type) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.hasProjector = hasProjector;
        this.hasComputer = hasComputer;
        this.type = type;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean hasProjector() {
        return hasProjector;
    }

    public void setHasProjector(boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public boolean hasComputer() {
        return hasComputer;
    }

    public void setHasComputer(boolean hasComputer) {
        this.hasComputer = hasComputer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name + (location != null ? " (" + location + ")" : "") + " - " + type;
    }
} 