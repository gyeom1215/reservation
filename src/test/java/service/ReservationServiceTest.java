package service;

import model.common.Reservation;
import model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceTest {
    private ReservationService reservationService;
    private User testStudent;
    private User testProfessor;
    private User testTA;
    
    @BeforeEach
    public void setup() {
        reservationService = new ReservationServiceImpl(); // 실제 구현체로 변경 필요
        testStudent = new User("student1", "pwd1", "학생1", "컴퓨터공학과", "student");
        testProfessor = new User("prof1", "pwd2", "교수1", "컴퓨터공학과", "professor");
        testTA = new User("ta1", "pwd3", "조교1", "컴퓨터공학과", "ta");
    }
    
    @Test
    public void testMakeReservation() {
        List<String> timeSlots = Arrays.asList("10:00", "11:00");
        Reservation reservation = new Reservation(
            testStudent.getName(),
            testStudent.getRole(),
            "일반강의실",
            101,
            "2024-05-27",
            timeSlots,
            "대기중"
        );
        
        assertTrue(reservationService.makeReservation(reservation));
        
        List<Reservation> userReservations = reservationService.getUserReservations(
            testStudent.getName(),
            testStudent.getRole()
        );
        assertFalse(userReservations.isEmpty());
        assertEquals(1, userReservations.size());
    }
    
    @Test
    public void testRoomAvailability() {
        List<String> timeSlots = Arrays.asList("10:00", "11:00");
        assertTrue(reservationService.isRoomAvailable(101, "2024-05-27", timeSlots, "일반강의실"));
        
        // 예약 생성
        Reservation reservation = new Reservation(
            testStudent.getName(),
            testStudent.getRole(),
            "일반강의실",
            101,
            "2024-05-27",
            timeSlots,
            "대기중"
        );
        reservationService.makeReservation(reservation);
        
        // 같은 시간대에 대한 중복 예약 확인
        assertFalse(reservationService.isRoomAvailable(101, "2024-05-27", timeSlots, "일반강의실"));
    }
    
    @Test
    public void testReservationApproval() {
        List<String> timeSlots = Arrays.asList("10:00", "11:00");
        Reservation reservation = new Reservation(
            testStudent.getName(),
            testStudent.getRole(),
            "일반강의실",
            101,
            "2024-05-27",
            timeSlots,
            "대기중"
        );
        
        reservationService.makeReservation(reservation);
        assertTrue(reservationService.updateReservationState(reservation, "승인됨"));
        
        List<Reservation> userReservations = reservationService.getUserReservations(
            testStudent.getName(),
            testStudent.getRole()
        );
        assertEquals("승인됨", userReservations.get(0).getState());
    }
    
    @Test
    public void testCancelReservation() {
        List<String> timeSlots = Arrays.asList("10:00", "11:00");
        Reservation reservation = new Reservation(
            testStudent.getName(),
            testStudent.getRole(),
            "일반강의실",
            101,
            "2024-05-27",
            timeSlots,
            "승인됨"
        );
        
        reservationService.makeReservation(reservation);
        assertTrue(reservationService.cancelReservation(reservation));
        
        List<Reservation> userReservations = reservationService.getUserReservations(
            testStudent.getName(),
            testStudent.getRole()
        );
        assertTrue(userReservations.isEmpty());
    }
} 