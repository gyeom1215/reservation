package service;

import model.common.Reservation;
import model.user.User;
import java.util.List;

public interface ReservationService {
    // 새로운 예약 생성
    boolean makeReservation(Reservation reservation);
    
    // 특정 사용자의 모든 예약 조회
    List<Reservation> getUserReservations(String name, String role);
    
    // 예약 취소
    boolean cancelReservation(Reservation reservation);
    
    // 모든 예약 조회 (관리자/조교용)
    List<Reservation> getAllReservations();
    
    // 예약 상태 업데이트
    boolean updateReservationState(Reservation reservation, String newState);
    
    // 특정 시간대에 강의실 사용 가능 여부 확인
    boolean isRoomAvailable(int roomNumber, String day, List<String> timeSlots, String roomType);
    
    // 사용자 등록
    boolean registerUser(User user);
    
    // 모든 사용자 조회
    List<User> getAllUsers();
    
    // 사용자 정보 업데이트
    boolean updateUser(User user);
    
    // 사용자 삭제
    boolean deleteUser(String userId);
} 