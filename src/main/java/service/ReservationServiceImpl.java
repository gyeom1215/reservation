package service;

import model.common.Reservation;
import model.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationServiceImpl implements ReservationService {
    private List<Reservation> reservations;
    private List<User> users;

    public ReservationServiceImpl() {
        this.reservations = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    @Override
    public boolean makeReservation(Reservation reservation) {
        if (isRoomAvailable(reservation.getRoomNumber(), 
                          reservation.getDay(), 
                          reservation.getTimeSlots(), 
                          reservation.getRoomType())) {
            return reservations.add(reservation);
        }
        return false;
    }

    @Override
    public List<Reservation> getUserReservations(String name, String role) {
        return reservations.stream()
            .filter(r -> r.getName().equals(name) && r.getRole().equals(role))
            .collect(Collectors.toList());
    }

    @Override
    public boolean cancelReservation(Reservation reservation) {
        return reservations.removeIf(r -> 
            r.getName().equals(reservation.getName()) &&
            r.getRoomNumber() == reservation.getRoomNumber() &&
            r.getDay().equals(reservation.getDay()) &&
            r.getTimeSlots().equals(reservation.getTimeSlots())
        );
    }

    @Override
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    @Override
    public boolean updateReservationState(Reservation reservation, String newState) {
        for (Reservation r : reservations) {
            if (r.getName().equals(reservation.getName()) &&
                r.getRoomNumber() == reservation.getRoomNumber() &&
                r.getDay().equals(reservation.getDay()) &&
                r.getTimeSlots().equals(reservation.getTimeSlots())) {
                r.setState(newState);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRoomAvailable(int roomNumber, String day, List<String> timeSlots, String roomType) {
        return reservations.stream()
            .filter(r -> r.getRoomNumber() == roomNumber &&
                        r.getDay().equals(day) &&
                        r.getRoomType().equals(roomType))
            .noneMatch(r -> hasTimeConflict(r.getTimeSlots(), timeSlots));
    }

    private boolean hasTimeConflict(List<String> existingSlots, List<String> newSlots) {
        return existingSlots.stream().anyMatch(newSlots::contains);
    }

    @Override
    public boolean registerUser(User user) {
        if (users.stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            return false;
        }
        return users.add(user);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public boolean updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteUser(String userId) {
        return users.removeIf(u -> u.getId().equals(userId));
    }
} 