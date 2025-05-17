package com.example.classroomreservation.util;

import com.example.classroomreservation.model.Reservation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JsonDataHandlerTest {

    @TempDir
    Path tempDir; // 각 테스트 메소드마다 새로운 임시 디렉토리 생성

    private JsonDataHandler jsonDataHandler;
    private Path testFilePath;

    @BeforeEach
    void setUp() {
        testFilePath = tempDir.resolve("test_reservations.json");
        jsonDataHandler = new JsonDataHandler(testFilePath.toString());
    }

    @Test
    void testSaveAndLoadEmptyReservations() throws IOException {
        List<Reservation> emptyList = new ArrayList<>();
        jsonDataHandler.saveReservations(emptyList);
        assertTrue(Files.exists(testFilePath), "JSON 파일이 생성되어야 합니다.");

        List<Reservation> loadedList = jsonDataHandler.loadReservations();
        assertNotNull(loadedList, "로드된 리스트는 null이 아니어야 합니다.");
        assertTrue(loadedList.isEmpty(), "로드된 리스트는 비어 있어야 합니다.");
    }

    @Test
    void testSaveAndLoadMultipleReservations() throws IOException {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation(UUID.randomUUID().toString(), "C101", "student1", "2024-01-01", "09:00", "10:00", "Study"));
        reservations.add(new Reservation(UUID.randomUUID().toString(), "C102", "student2", "2024-01-01", "10:00", "11:00", "Meeting"));

        jsonDataHandler.saveReservations(reservations);
        assertTrue(Files.exists(testFilePath));

        List<Reservation> loadedReservations = jsonDataHandler.loadReservations();
        assertNotNull(loadedReservations);
        assertEquals(2, loadedReservations.size(), "두 개의 예약이 로드되어야 합니다.");
        // 내용까지 비교하려면 Reservation 클래스에 equals/hashCode가 잘 구현되어 있어야 함
        assertTrue(loadedReservations.containsAll(reservations) && reservations.containsAll(loadedReservations), "저장된 예약과 로드된 예약 내용이 일치해야 합니다.");
    }

    @Test
    void testLoadNonExistentFile() {
        // setUp에서 파일이 생성되지 않은 상태 (또는 파일을 삭제하고 테스트)
        try {
            if (Files.exists(testFilePath)) {
                Files.delete(testFilePath);
            }
        } catch (IOException e) {
            fail("테스트 파일 삭제 중 오류: " + e.getMessage());
        }
        List<Reservation> loadedList = jsonDataHandler.loadReservations();
        assertNotNull(loadedList);
        assertTrue(loadedList.isEmpty(), "존재하지 않는 파일 로드 시 빈 리스트가 반환되어야 합니다.");
    }
    
    @Test
    void testLoadMalformedJsonFile() throws IOException {
        Files.writeString(testFilePath, "this is not a valid json string {{{{");
        List<Reservation> loadedList = jsonDataHandler.loadReservations();
        assertNotNull(loadedList);
        assertTrue(loadedList.isEmpty(), "잘못된 형식의 JSON 파일 로드 시 빈 리스트가 반환되어야 합니다 (또는 특정 예외 처리).");
    }


    @AfterEach
    void tearDown() throws IOException {
        // @TempDir 사용 시 자동 정리되므로 명시적 삭제 불필요.
        // 단, 파일이 남아있는지 확인하는 테스트 등을 위해선 필요할 수 있음.
        // Files.deleteIfExists(testFilePath);
    }
}