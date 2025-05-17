package com.example.classroomreservation.integration;

import com.example.classroomreservation.model.Reservation;
import com.example.classroomreservation.service.ReservationService;
import com.example.classroomreservation.util.JsonDataHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationSystemIntegrationTest {

    @TempDir
    Path tempDir;

    private ReservationService reservationService;
    private JsonDataHandler jsonDataHandler;
    private Path testJsonFile;

    private final String studentA = "studentA";
    private final String studentB = "studentB";
    private final String classroom = "C202";
    private final String date = "2024-08-01";

    @BeforeEach
    void setUp() throws IOException {
        testJsonFile = tempDir.resolve("integration_test_reservations.json");
        if (Files.exists(testJsonFile)) {
            Files.delete(testJsonFile);
        }
        Files.createFile(testJsonFile); // Ensure file exists for JsonDataHandler to not throw error on first load in constructor

        jsonDataHandler = new JsonDataHandler(testJsonFile.toString());
        // 새로운 서비스 인스턴스를 만들어 파일로부터 로드
        reservationService = new ReservationService(jsonDataHandler);
    }
    
    @AfterEach
    void tearDown() throws IOException {
        // @TempDir가 자동 정리하지만, 명시적으로 파일 내용 확인 후 삭제하고 싶을 때 사용
        // Files.deleteIfExists(testJsonFile);
    }


    @Test
    void fullReservationCycle_MakeReservation_CheckFile_CancelReservation_CheckFile() {
        // 1. 학생 A가 예약
        boolean makeResult = reservationService.makeReservation(classroom, studentA, date, "10:00", "11:00", "Project Meeting");
        assertTrue(makeResult, "학생 A의 예약이 성공해야 합니다.");

        // 2. 파일 확인 (서비스를 새로 생성하여 파일로부터 로드)
        ReservationService serviceAfterSave = new ReservationService(new JsonDataHandler(testJsonFile.toString()));
        List<Reservation> reservationsAfterSave = serviceAfterSave.getAllReservations();
        assertEquals(1, reservationsAfterSave.size(), "파일에 예약이 1개 저장되어야 합니다.");
        Reservation savedReservation = reservationsAfterSave.get(0);
        assertEquals(studentA, savedReservation.getStudentId());
        assertEquals("10:00", savedReservation.getStartTime());

        // 3. 학생 A가 자신의 예약 취소
        // 원래 reservationService 인스턴스 사용 (메모리 상태와 파일 상태 동기화됨)
        boolean cancelResult = reservationService.cancelReservation(savedReservation.getId(), studentA);
        assertTrue(cancelResult, "학생 A가 자신의 예약을 취소할 수 있어야 합니다.");

        // 4. 파일 재확인 (서비스를 새로 생성하여 파일로부터 로드)
        ReservationService serviceAfterCancel = new ReservationService(new JsonDataHandler(testJsonFile.toString()));
        assertTrue(serviceAfterCancel.getAllReservations().isEmpty(), "예약 취소 후 파일에 예약이 없어야 합니다.");
    }

    @Test
    void reservationPersistence_MakeReservation_ReloadService_VerifyReservationExists() {
        // 1. 학생 B가 예약
        String purpose = "Individual Study";
        boolean makeResult = reservationService.makeReservation(classroom, studentB, date, "14:00", "15:00", purpose);
        assertTrue(makeResult);
        String reservationId = reservationService.getMyReservations(studentB).get(0).getId();


        // 2. 새로운 서비스 인스턴스 생성 (파일에서 로드 시뮬레이션)
        ReservationService newServiceInstance = new ReservationService(new JsonDataHandler(testJsonFile.toString()));
        List<Reservation> studentBReservations = newServiceInstance.getMyReservations(studentB);

        assertEquals(1, studentBReservations.size(), "재로드 후 학생 B의 예약이 존재해야 합니다.");
        Reservation loadedReservation = studentBReservations.get(0);
        assertEquals(reservationId, loadedReservation.getId());
        assertEquals(classroom, loadedReservation.getClassroomId());
        assertEquals(date, loadedReservation.getDate());
        assertEquals("14:00", loadedReservation.getStartTime());
        assertEquals(purpose, loadedReservation.getPurpose());
    }
    
    @Test
    void studentCannotCancelOthersReservation_Integration() {
        // 학생 A가 예약
        reservationService.makeReservation(classroom, studentA, date, "12:00", "13:00", "A's booking");
        Reservation reservationA = reservationService.getMyReservations(studentA).get(0);

        // 파일에 저장되었는지 확인 (선택적 중간 검증)
        ReservationService tempService = new ReservationService(new JsonDataHandler(testJsonFile.toString()));
        assertNotNull(tempService.getReservationById(reservationA.getId()), "예약이 파일에 있어야 함.");

        // 학생 B가 학생 A의 예약을 취소 시도
        boolean cancelAttemptByB = reservationService.cancelReservation(reservationA.getId(), studentB);
        assertFalse(cancelAttemptByB, "학생 B는 학생 A의 예약을 취소할 수 없어야 합니다.");

        // 파일 확인: 예약이 여전히 존재해야 함
        ReservationService serviceAfterAttempt = new ReservationService(new JsonDataHandler(testJsonFile.toString()));
        assertNotNull(serviceAfterAttempt.getReservationById(reservationA.getId()), "취소 시도 실패 후 예약은 파일에 여전히 존재해야 합니다.");
        assertEquals(1, serviceAfterAttempt.getAllReservations().size());
    }

    @Test
    void conflictingReservations_SecondAttemptFails_FileSystemCheck() {
        // 첫 번째 예약 (학생 A)
        boolean firstReservation = reservationService.makeReservation(classroom, studentA, date, "15:00", "16:00", "First");
        assertTrue(firstReservation);

        // 두 번째 예약 시도 (학생 B, 시간 겹침)
        boolean secondReservationAttempt = reservationService.makeReservation(classroom, studentB, date, "15:30", "16:30", "Second Conflict");
        assertFalse(secondReservationAttempt, "겹치는 예약은 실패해야 합니다.");

        // 파일 시스템 확인 (새로운 서비스 인스턴스로)
        ReservationService finalServiceState = new ReservationService(new JsonDataHandler(testJsonFile.toString()));
        List<Reservation> allReservations = finalServiceState.getAllReservations();
        assertEquals(1, allReservations.size(), "파일에는 성공한 첫 번째 예약만 있어야 합니다.");
        assertEquals(studentA, allReservations.get(0).getStudentId());
        assertEquals("15:00", allReservations.get(0).getStartTime());
    }
}