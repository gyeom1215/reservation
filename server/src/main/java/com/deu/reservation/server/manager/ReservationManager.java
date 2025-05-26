package com.deu.reservation.server.manager;

import com.deu.reservation.server.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ReservationManager {
    private static final String RESERVATION_FILE = "reservations.json";
    private static final Map<String, Reservation> reservations = new ConcurrentHashMap<>();
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();
    private static boolean loaded = false;

    // 초기 강의실 데이터 로드
    static {
        // 일반강의실
        rooms.put("R912", new Room("R912", "912호", 40, "9층", Room.RoomType.LECTURE));
        rooms.put("R913", new Room("R913", "913호", 40, "9층", Room.RoomType.LECTURE));
        rooms.put("R914", new Room("R914", "914호", 40, "9층", Room.RoomType.LECTURE));
        rooms.put("R908", new Room("R908", "908호", 40, "9층", Room.RoomType.LECTURE));
        
        // 실습실
        rooms.put("R911", new Room("R911", "911호", 30, "9층", Room.RoomType.LAB));
        rooms.put("R915", new Room("R915", "915호", 30, "9층", Room.RoomType.LAB));
        rooms.put("R916", new Room("R916", "916호", 30, "9층", Room.RoomType.LAB));
        rooms.put("R918", new Room("R918", "918호", 30, "9층", Room.RoomType.LAB));
    }

    // 예약 생성
    public static Reservation createReservation(String userId, String roomId, 
            LocalDateTime startTime, LocalDateTime endTime, String purpose, User user) {
        if (!isTimeSlotAvailable(roomId, startTime, endTime)) {
            return null;
        }

        String id = UUID.randomUUID().toString();
        Reservation reservation = new Reservation(id, userId, roomId, startTime, endTime, purpose);
        
        // 교수는 자동 승인, 학생은 대기 상태
        if (user.getRole() == Role.PROFESSOR) {
            reservation.setStatus(ReservationStatus.APPROVED);
            reservation.setApprovedBy(userId);
        } else {
            reservation.setStatus(ReservationStatus.PENDING);
        }

        reservations.put(id, reservation);
        saveReservations();
        return reservation;
    }

    // 예약 승인 (TA 조교용)
    public static boolean approveReservation(String reservationId, String approverId) {
        Reservation reservation = reservations.get(reservationId);
        if (reservation != null && reservation.getStatus() == ReservationStatus.PENDING) {
            reservation.setStatus(ReservationStatus.APPROVED);
            reservation.setApprovedBy(approverId);
            saveReservations();
            return true;
        }
        return false;
    }

    // 예약 거절 (TA 조교용)
    public static boolean rejectReservation(String reservationId) {
        Reservation reservation = reservations.get(reservationId);
        if (reservation != null && reservation.getStatus() == ReservationStatus.PENDING) {
            reservation.setStatus(ReservationStatus.REJECTED);
            saveReservations();
            return true;
        }
        return false;
    }

    // 예약 취소
    public static boolean cancelReservation(String reservationId) {
        Reservation reservation = reservations.get(reservationId);
        if (reservation != null && reservation.getStatus() != ReservationStatus.CANCELLED) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            saveReservations();
            return true;
        }
        return false;
    }

    // 사용자의 예약 목록 조회
    public static List<Reservation> getUserReservations(String userId) {
        return reservations.values().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    // 모든 예약 목록 조회 (교수/조교용)
    public static List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations.values());
    }

    // 강의실 목록 조회
    public static List<Room> getRooms() {
        return new ArrayList<>(rooms.values());
    }

    // 강의실 유형별 목록 조회
    public static List<Room> getRoomsByType(Room.RoomType type) {
        return rooms.values().stream()
                .filter(r -> r.getRoomType() == type)
                .collect(Collectors.toList());
    }

    // 특정 시간대의 예약 가능 여부 확인
    private static boolean isTimeSlotAvailable(String roomId, LocalDateTime startTime, LocalDateTime endTime) {
        return reservations.values().stream()
                .filter(r -> r.getRoomId().equals(roomId))
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED && r.getStatus() != ReservationStatus.REJECTED)
                .noneMatch(r -> (startTime.isBefore(r.getEndTime()) && endTime.isAfter(r.getStartTime())));
    }

    // 예약 데이터 저장
    private static void saveReservations() {
        try (Writer writer = new FileWriter(RESERVATION_FILE)) {
            gson.toJson(new ArrayList<>(reservations.values()), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 예약 데이터 로드
    public static void loadReservations() {
        if (loaded) return;
        
        File file = new File(RESERVATION_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                List<Reservation> reservationList = gson.fromJson(reader, 
                    new TypeToken<List<Reservation>>(){}.getType());
                if (reservationList != null) {
                    for (Reservation r : reservationList) {
                        reservations.put(r.getId(), r);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loaded = true;
    }
} 