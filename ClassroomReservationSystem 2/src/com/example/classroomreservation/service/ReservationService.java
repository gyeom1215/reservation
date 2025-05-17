package com.example.classroomreservation.service;

import com.example.classroomreservation.model.Reservation;
import com.example.classroomreservation.util.JsonDataHandler;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReservationService {
    private List<Reservation> reservations;
    private final JsonDataHandler jsonDataHandler;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static final String[] AVAILABLE_TIMES = {
        "09:00", "10:00", "11:00", "12:00", "13:00",
        "14:00", "15:00", "16:00", "17:00"
    };
    public static final String[] CLASSROOM_IDS = {"C101", "C102", "C201", "C202", "C301"};

    // 운영용 생성자
    public ReservationService() {
        this(new JsonDataHandler("reservations.json"));
    }

    // 테스트 및 주입용 생성자
    public ReservationService(JsonDataHandler jsonDataHandler) {
        this.jsonDataHandler = jsonDataHandler;
        this.reservations = this.jsonDataHandler.loadReservations();
    }

    public synchronized boolean makeReservation(String classroomId, String studentId, String date, String startTime, String endTime, String purpose) {
        if (!isTimeSlotAvailable(classroomId, date, startTime, endTime)) {
            return false;
        }
        // 시작 시간이 종료 시간보다 늦거나 같은 경우 방지
        LocalTime newStart = LocalTime.parse(startTime, timeFormatter);
        LocalTime newEnd = LocalTime.parse(endTime, timeFormatter);
        if (!newStart.isBefore(newEnd)) {
            return false;
        }

        String id = UUID.randomUUID().toString();
        Reservation newReservation = new Reservation(id, classroomId, studentId, date, startTime, endTime, purpose);
        reservations.add(newReservation);
        jsonDataHandler.saveReservations(reservations);
        return true;
    }

    public synchronized boolean cancelReservation(String reservationId, String studentId) {
        Reservation toRemove = reservations.stream()
                .filter(r -> r.getId().equals(reservationId) && r.getStudentId().equals(studentId))
                .findFirst()
                .orElse(null);

        if (toRemove != null) {
            reservations.remove(toRemove);
            jsonDataHandler.saveReservations(reservations);
            return true;
        }
        return false;
    }
    
    // UI에서 학생 ID를 기반으로 "내 예약만 취소 가능"을 확인하므로,
    // 서비스단에서는 전달된 studentId가 예약의 studentId와 일치하는지만 확인하면 됨.
    public Reservation getReservationById(String reservationId) {
        return reservations.stream()
                .filter(r -> r.getId().equals(reservationId))
                .findFirst()
                .orElse(null);
    }


    public List<Reservation> getMyReservations(String studentId) {
        return reservations.stream()
                .filter(r -> r.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public List<Reservation> getReservationsByClassroomAndDate(String classroomId, String date) {
        return reservations.stream()
                .filter(r -> r.getClassroomId().equals(classroomId) && r.getDate().equals(date))
                .sorted((r1, r2) -> LocalTime.parse(r1.getStartTime(), timeFormatter).compareTo(LocalTime.parse(r2.getStartTime(), timeFormatter)))
                .collect(Collectors.toList());
    }

    public boolean isTimeSlotAvailable(String classroomId, String date, String newStartTimeStr, String newEndTimeStr) {
        LocalTime newStart = LocalTime.parse(newStartTimeStr, timeFormatter);
        LocalTime newEnd = LocalTime.parse(newEndTimeStr, timeFormatter);

        if (newStart.isAfter(newEnd) || newStart.equals(newEnd)) {
            return false;
        }

        for (Reservation existing : reservations) {
            if (existing.getClassroomId().equals(classroomId) && existing.getDate().equals(date)) {
                LocalTime existingStart = LocalTime.parse(existing.getStartTime(), timeFormatter);
                LocalTime existingEnd = LocalTime.parse(existing.getEndTime(), timeFormatter);
                if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                    return false;
                }
            }
        }
        return true;
    }

    public String[] getAvailableClassrooms() {
        return CLASSROOM_IDS;
    }

    public String[] getAvailableTimeSlots() {
        return AVAILABLE_TIMES;
    }

    // 테스트를 위해 예약 목록을 직접 설정하거나 가져올 수 있는 메소드 (선택 사항)
    
       public void setReservationsForTest(List<Reservation> testReservations) {
        this.reservations = new ArrayList<>(testReservations);
        jsonDataHandler.saveReservations(this.reservations);
    }

    public List<Reservation> getInternalReservationsListForTest() {
        return this.reservations;
    }
}