package com.example.classroomreservation.service;

import com.example.classroomreservation.model.Reservation;
import com.example.classroomreservation.util.JsonDataHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension; // Mockito는 여기선 직접 안쓰지만, 필요시 추가

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Mockito 사용할 경우
class ReservationServiceTest {

    @TempDir
    Path tempDir;

    private ReservationService reservationService;
    private JsonDataHandler jsonDataHandler;
    private Path testJsonFile;

    private final String studentId1 = "student1";
    private final String studentId2 = "student2";
    private final String classroomId1 = "C101";
    private final String date1 = "2024-07-20";

    @BeforeEach
    void setUp() throws IOException {
        testJsonFile = tempDir.resolve("service_test_reservations.json");
        // 각 테스트 전에 파일을 비움 (새로운 상태에서 시작)
        if (Files.exists(testJsonFile)) {
            Files.delete(testJsonFile);
        }
        Files.createFile(testJsonFile); // 빈 파일 생성

        jsonDataHandler = new JsonDataHandler(testJsonFile.toString());
        reservationService = new ReservationService(jsonDataHandler); // 테스트용 핸들러 주입
    }

    @Test
    void makeReservation_Success() {
        boolean result = reservationService.makeReservation(classroomId1, studentId1, date1, "09:00", "10:00", "Study");
        assertTrue(result, "예약이 성공해야 합니다.");
        assertEquals(1, reservationService.getAllReservations().size(), "예약 목록에 1개가 추가되어야 합니다.");
        // 파일 저장 확인
        List<Reservation> loaded = jsonDataHandler.loadReservations();
        assertEquals(1, loaded.size());
        assertEquals(studentId1, loaded.get(0).getStudentId());
    }

    @Test
    void makeReservation_TimeConflict() {
        reservationService.makeReservation(classroomId1, studentId1, date1, "09:00", "10:00", "Study");
        boolean resultConflict = reservationService.makeReservation(classroomId1, studentId2, date1, "09:30", "10:30", "Meeting");
        assertFalse(resultConflict, "시간이 겹치는 예약은 실패해야 합니다.");
        assertEquals(1, reservationService.getAllReservations().size(), "겹치는 예약 시도는 추가되지 않아야 합니다.");
    }
    
    @Test
    void makeReservation_SameStartAndEndTime_ShouldFail() {
        boolean result = reservationService.makeReservation(classroomId1, studentId1, date1, "10:00", "10:00", "Quick check");
        assertFalse(result, "시작 시간과 종료 시간이 같으면 예약에 실패해야 합니다.");
        assertTrue(reservationService.getAllReservations().isEmpty(), "잘못된 시간 예약은 추가되지 않아야 합니다.");
    }

    @Test
    void makeReservation_StartTimeAfterEndTime_ShouldFail() {
        boolean result = reservationService.makeReservation(classroomId1, studentId1, date1, "11:00", "10:00", "Time travel");
        assertFalse(result, "시작 시간이 종료 시간보다 늦으면 예약에 실패해야 합니다.");
        assertTrue(reservationService.getAllReservations().isEmpty(), "잘못된 시간 예약은 추가되지 않아야 합니다.");
    }


    @Test
    void cancelReservation_Success() {
        reservationService.makeReservation(classroomId1, studentId1, date1, "11:00", "12:00", "Presentation Prep");
        Reservation reservation = reservationService.getMyReservations(studentId1).get(0);
        assertNotNull(reservation, "예약이 생성되었어야 합니다.");

        boolean cancelResult = reservationService.cancelReservation(reservation.getId(), studentId1);
        assertTrue(cancelResult, "본인 예약은 취소되어야 합니다.");
        assertTrue(reservationService.getMyReservations(studentId1).isEmpty(), "취소 후 해당 학생의 예약이 없어야 합니다.");
        // 파일 저장 확인
        List<Reservation> loaded = jsonDataHandler.loadReservations();
        assertTrue(loaded.isEmpty());
    }

    @Test
    void cancelReservation_NotOwnReservation_ShouldFail() {
        // studentId1이 예약
        reservationService.makeReservation(classroomId1, studentId1, date1, "13:00", "14:00", "Team Meeting");
        Reservation reservation = reservationService.getMyReservations(studentId1).get(0);

        // studentId2가 취소 시도
        boolean cancelResult = reservationService.cancelReservation(reservation.getId(), studentId2);
        assertFalse(cancelResult, "다른 사람의 예약은 취소할 수 없어야 합니다.");
        assertEquals(1, reservationService.getAllReservations().size(), "예약은 여전히 존재해야 합니다.");
    }

    @Test
    void cancelReservation_NonExistentReservation_ShouldFail() {
        boolean cancelResult = reservationService.cancelReservation("non-existent-id", studentId1);
        assertFalse(cancelResult, "존재하지 않는 예약은 취소할 수 없어야 합니다.");
    }

    @Test
    void getMyReservations() {
        reservationService.makeReservation(classroomId1, studentId1, date1, "14:00", "15:00", "My Study 1");
        reservationService.makeReservation(classroomId1, studentId1, date1, "15:00", "16:00", "My Study 2");
        reservationService.makeReservation("C102", studentId2, date1, "14:00", "15:00", "Other's Study");

        List<Reservation> student1Reservations = reservationService.getMyReservations(studentId1);
        assertEquals(2, student1Reservations.size(), "studentId1의 예약은 2개여야 합니다.");
        assertTrue(student1Reservations.stream().allMatch(r -> r.getStudentId().equals(studentId1)));

        List<Reservation> student2Reservations = reservationService.getMyReservations(studentId2);
        assertEquals(1, student2Reservations.size());
    }

    @Test
    void getAllReservations() {
        reservationService.makeReservation(classroomId1, studentId1, date1, "09:00", "10:00", "All Rsv 1");
        reservationService.makeReservation("C102", studentId2, date1, "10:00", "11:00", "All Rsv 2");
        assertEquals(2, reservationService.getAllReservations().size(), "모든 예약이 반환되어야 합니다.");
    }

    @Test
    void isTimeSlotAvailable_VariousScenarios() {
        // 초기 상태: C101, 2024-07-20, 10:00-11:00 예약
        reservationService.makeReservation(classroomId1, studentId1, date1, "10:00", "11:00", "Booked Slot");

        // 겹치는 경우
        assertFalse(reservationService.isTimeSlotAvailable(classroomId1, date1, "09:30", "10:30"), "완전히 겹침 (앞부분)");
        assertFalse(reservationService.isTimeSlotAvailable(classroomId1, date1, "10:30", "11:30"), "완전히 겹침 (뒷부분)");
        assertFalse(reservationService.isTimeSlotAvailable(classroomId1, date1, "10:00", "11:00"), "완전히 동일");
        assertFalse(reservationService.isTimeSlotAvailable(classroomId1, date1, "09:00", "12:00"), "기존 예약을 포함");
        assertFalse(reservationService.isTimeSlotAvailable(classroomId1, date1, "10:15", "10:45"), "기존 예약 내부에 포함");

        // 안 겹치는 경우
        assertTrue(reservationService.isTimeSlotAvailable(classroomId1, date1, "09:00", "10:00"), "이전 시간");
        assertTrue(reservationService.isTimeSlotAvailable(classroomId1, date1, "11:00", "12:00"), "이후 시간");
        assertTrue(reservationService.isTimeSlotAvailable("C102", date1, "10:00", "11:00"), "다른 강의실"); // 다른 강의실은 가능
        assertTrue(reservationService.isTimeSlotAvailable(classroomId1, "2024-07-21", "10:00", "11:00"), "다른 날짜"); // 다른 날짜는 가능

        // 잘못된 시간
        assertFalse(reservationService.isTimeSlotAvailable(classroomId1, date1, "10:00", "09:00"), "시작 > 종료");
        assertFalse(reservationService.isTimeSlotAvailable(classroomId1, date1, "10:00", "10:00"), "시작 == 종료");
    }
    
    @Test
    void getReservationsByClassroomAndDate_ReturnsCorrectlySortedList() {
        // 다른 날짜, 다른 강의실 예약
        reservationService.makeReservation(classroomId1, studentId1, "2024-07-21", "10:00", "11:00", "Other Day");
        reservationService.makeReservation("C999", studentId1, date1, "10:00", "11:00", "Other Classroom");

        // 테스트 대상 날짜 및 강의실 예약 (순서 섞어서 추가)
        reservationService.makeReservation(classroomId1, studentId1, date1, "14:00", "15:00", "Afternoon");
        reservationService.makeReservation(classroomId1, studentId2, date1, "10:00", "11:00", "Morning");
        reservationService.makeReservation(classroomId1, studentId1, date1, "09:00", "09:30", "Early Morning");
        
        List<Reservation> result = reservationService.getReservationsByClassroomAndDate(classroomId1, date1);
        
        assertEquals(3, result.size(), "해당 강의실, 날짜의 예약은 3개여야 합니다.");
        assertEquals("09:00", result.get(0).getStartTime());
        assertEquals("Early Morning", result.get(0).getPurpose());
        assertEquals("10:00", result.get(1).getStartTime());
        assertEquals("Morning", result.get(1).getPurpose());
        assertEquals("14:00", result.get(2).getStartTime());
        assertEquals("Afternoon", result.get(2).getPurpose());
    }
}