package client;

import model.common.Reservation;
import model.user.User;
import service.ReservationService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationClient {
    private static ReservationClient instance;
    private final ReservationService reservationService;
    private static final Logger LOGGER = Logger.getLogger(ReservationClient.class.getName());

    private ReservationClient(ReservationService reservationService) {
        this.reservationService = reservationService;
        System.out.println("ReservationClient 초기화 완료");
    }

    public static ReservationClient getInstance(ReservationService reservationService) {
        if (instance == null) {
            instance = new ReservationClient(reservationService);
        }
        return instance;
    }

    public List<User> getAllUsers() {
        try {
            System.out.println("모든 사용자 정보 조회 요청");
            return reservationService.getAllUsers();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting all users: " + e.getMessage(), e);
            System.err.println("사용자 정보 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean registerUser(User user) {
        try {
            System.out.println("사용자 등록 요청: " + user.getName() + "님");
            return reservationService.registerUser(user);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error registering user: " + e.getMessage(), e);
            System.err.println("사용자 등록 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean makeReservation(Reservation reservation) {
        try {
            System.out.println("예약 요청: " + reservation.getName() + "님의 " + 
                reservation.getRoomType() + " " + reservation.getRoomNumber() + "호 예약");
            return reservationService.makeReservation(reservation);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error making reservation: " + e.getMessage(), e);
            System.err.println("예약 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Reservation> getUserReservations(String name, String role) {
        try {
            System.out.println("예약 조회 요청: " + name + "님의 예약 목록");
            return reservationService.getUserReservations(name, role);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving reservations: " + e.getMessage(), e);
            System.err.println("예약 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean cancelReservation(Reservation reservation) {
        try {
            System.out.println("예약 취소 요청: " + reservation.getName() + "님의 " + 
                reservation.getRoomType() + " " + reservation.getRoomNumber() + "호 예약");
            return reservationService.cancelReservation(reservation);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cancelling reservation: " + e.getMessage(), e);
            System.err.println("예약 취소 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Reservation> getAllReservations() {
        try {
            System.out.println("전체 예약 목록 조회 요청");
            return reservationService.getAllReservations();
        } catch (Exception e) {
            System.err.println("전체 예약 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean updateReservationState(Reservation reservation, String newState) {
        try {
            System.out.println("예약 상태 업데이트 요청: " + reservation.getName() + "님의 예약을 " + 
                newState + " 상태로 변경");
            return reservationService.updateReservationState(reservation, newState);
        } catch (Exception e) {
            System.err.println("예약 상태 업데이트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isRoomAvailable(int roomNumber, String day, List<String> timeSlots, String roomType) {
        try {
            System.out.println("강의실 가용성 확인 요청: " + roomType + " " + roomNumber + "호 " + 
                day + " " + String.join(", ", timeSlots));
            return reservationService.isRoomAvailable(roomNumber, day, timeSlots, roomType);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking room availability: " + e.getMessage(), e);
            System.err.println("강의실 가용성 확인 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) {
        try {
            LOGGER.info("사용자 정보 수정 시도: " + user.getId());
            return reservationService.updateUser(user);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "사용자 정보 수정 중 오류 발생: " + e.getMessage(), e);
            System.err.println("사용자 정보 수정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        try {
            LOGGER.info("사용자 삭제 시도: " + userId);
            return reservationService.deleteUser(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "사용자 삭제 중 오류 발생: " + e.getMessage(), e);
            System.err.println("사용자 삭제 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 