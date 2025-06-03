package model.reservation;

import model.common.Reservation;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ReservationTest {
    
    @Test
    public void testReservationCreation() {
        List<String> timeSlots = Arrays.asList("10:00", "11:00");
        Reservation reservation = new Reservation("테스트사용자", "student", "일반강의실", 101, "2024-05-27", timeSlots, "대기중");
        
        assertEquals("테스트사용자", reservation.getName());
        assertEquals("student", reservation.getRole());
        assertEquals("일반강의실", reservation.getRoomType());
        assertEquals(101, reservation.getRoomNumber());
        assertEquals("2024-05-27", reservation.getDay());
        assertEquals(timeSlots, reservation.getTimeSlots());
        assertEquals("대기중", reservation.getState());
    }
    
    @Test
    public void testReservationStateChange() {
        List<String> timeSlots = Arrays.asList("10:00", "11:00");
        Reservation reservation = new Reservation("테스트사용자", "student", "일반강의실", 101, "2024-05-27", timeSlots, "대기중");
        
        reservation.setState("승인됨");
        assertEquals("승인됨", reservation.getState());
        
        reservation.setState("거절됨");
        assertEquals("거절됨", reservation.getState());
    }
    
    @Test
    public void testTimeSlotModification() {
        List<String> initialTimeSlots = Arrays.asList("10:00", "11:00");
        Reservation reservation = new Reservation("테스트사용자", "student", "일반강의실", 101, "2024-05-27", initialTimeSlots, "대기중");
        
        List<String> newTimeSlots = Arrays.asList("13:00", "14:00", "15:00");
        reservation.setTimeSlots(newTimeSlots);
        
        assertEquals(newTimeSlots, reservation.getTimeSlots());
        assertEquals(3, reservation.getTimeSlots().size());
    }
} 