package com.deu.reservation.client.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    private final String id;
    private final String userId;
    private final String roomId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String purpose;
    private ReservationStatus status;
    private String roomName;
    private User user;
    private Room room;

    public Reservation(String id, String userId, String roomId, LocalDateTime startTime, 
                      LocalDateTime endTime, String purpose, ReservationStatus status) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = status;
    }

    public Reservation(User user, Room room, LocalDateTime startTime, 
                      LocalDateTime endTime, String purpose, ReservationStatus status) {
        this.id = java.util.UUID.randomUUID().toString();
        this.userId = user.getId();
        this.roomId = room.getId();
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = status;
        this.roomName = room.getName();
        this.user = user;
        this.room = room;
    }

    public Reservation(String id, String userId, String roomId, LocalDateTime startTime, 
            LocalDateTime endTime, String purpose, ReservationStatus status, User user, Room room) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = status;
        this.roomName = room.getName();
        this.user = user;
        this.room = room;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getRoomId() {
        return roomId;
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

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public User getUser() {
        return user;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return String.format("%s ~ %s - %s (%s)", 
            startTime.toString(), 
            endTime.toString(), 
            purpose,
            status.toString());
    }
} 