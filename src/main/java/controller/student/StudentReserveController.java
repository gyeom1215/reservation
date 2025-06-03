package controller.student;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import client.ReservationClient;

import model.room.RoomModel;
import model.room.RoomReservation;
import model.user.User;
import view.login.LoginView;
import view.student.StudentView;

import javax.swing.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class StudentReserveController {
    private final StudentView studentView;
    private final User user;
    private final String labRoomPath;
    private final String normalRoomPath;
    private final String reservationPath;
    private final ReservationClient reservationClient;

    public StudentReserveController(StudentView studentView, User user) {
        this(studentView, user, "src/Lab_room.json", "src/normal_room.json", "reservations.json");
    }

    public StudentReserveController(StudentView studentView, User user, String labRoomPath, String normalRoomPath, String reservationPath) {
        this.studentView = studentView;
        this.user = user;
        this.labRoomPath = labRoomPath;
        this.normalRoomPath = normalRoomPath;
        this.reservationPath = reservationPath;
        this.reservationClient = studentView.getReservationClient();

        studentView.setReservationHandler(this::reserveRoom);
        initListeners();
    }

    public void reserveRoom(model.common.Reservation reservation) {
        String jsonPath = reservation.getRoomType().equals("실습실") ? labRoomPath : normalRoomPath;
        RoomModel roomModel = studentView.getRoomModel();

        // 하나라도 예약 불가인 시간대가 있으면 예약 중단
        for (String timeSlot : reservation.getTimeSlots()) {
            if (!roomModel.isReservable(reservation.getRoomNumber(), reservation.getDay(), timeSlot)) {
                JOptionPane.showMessageDialog(null, "이미 예약된 시간대를 포함하고 있습니다.\n예약할 수 없습니다.");
                return;
            }
        }

        RoomReservation roomReservation = new RoomReservation(
                reservation.getRoomNumber(),
                reservation.getDay(),
                reservation.getTimeSlots(),
                reservation.getRoomType(),
                reservation.getState(),
                reservation.getName(),
                reservation.getRole()
        );

        // 예약이 완료된 모든 시간대를 "X"로 변경하고 파일에 바로 저장
        for (String timeSlot : reservation.getTimeSlots()) {
            roomModel.markReserved(reservation.getRoomNumber(), reservation.getDay(), timeSlot, jsonPath);
        }

        saveReservationEntry(new ReservationEntry(
                reservation.getName(),
                reservation.getRole(),
                reservation.getRoomType(),
                String.valueOf(reservation.getRoomNumber()),
                reservation.getDay(),
                reservation.getTimeSlots(),
                reservation.getState()
        ));

        JOptionPane.showMessageDialog(null, "예약이 완료되었습니다.");
    }

    private void initListeners() {
        studentView.getBtnStartReservation().addActionListener(e -> {
            String[] options = {"실습실", "일반실"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "예약할 강의실 유형을 선택하세요.",
                    "강의실 유형 선택",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            if (choice != JOptionPane.CLOSED_OPTION) {
                String jsonPath = (choice == 0) ? labRoomPath : normalRoomPath;
                String roomType = options[choice];
                startReservation(jsonPath, roomType);
            }
        });

        studentView.getBtnLogout().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "로그아웃하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                studentView.dispose(); // 현재 창 닫기
                new LoginView(reservationClient);         // 로그인 창 열기
            }
        });
    }

    public List<ReservationEntry> loadActiveReservations(String filePath) {
        Gson gson = new Gson();
        List<ReservationEntry> result = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath)) {
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<ReservationEntry>>() {}.getType();
            List<ReservationEntry> all = gson.fromJson(reader, listType);
            if (all != null) {
                // 현재 사용자의 예약만 필터링하고 취소된 예약은 제외
                result.addAll(all.stream()
                    .filter(r -> r.name.equals(user.getName()) && !"취소".equals(r.state))
                    .toList());
            }
        } catch (Exception e) {
            // 파일이 없거나 잘못됐을 때는 빈 리스트 반환
        }
        return result;
    }

    public void startReservation(String jsonPath, String roomType) {
        List<ReservationEntry> activeLabs = loadActiveReservations(labRoomPath);
        List<ReservationEntry> activeNormals = loadActiveReservations(normalRoomPath);
        studentView.showReservationUI(jsonPath, roomType);
    }

    public void saveReservationEntry(ReservationEntry entry) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            java.io.File file = new java.io.File(reservationPath);
            java.util.List<ReservationEntry> entries = new java.util.ArrayList<>();
            if (file.exists() && file.length() > 0) {
                try (java.io.FileReader reader = new java.io.FileReader(file)) {
                    java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<ReservationEntry>>() {}.getType();
                    List<ReservationEntry> existing = gson.fromJson(reader, listType);
                    if (existing != null) {
                        entries = new java.util.ArrayList<>(existing);
                    }
                }
            }

            // 동일한 예약이 있는지 확인하고 업데이트 또는 추가
            boolean found = false;
            for (int i = 0; i < entries.size(); i++) {
                ReservationEntry existing = entries.get(i);
                if (existing.name.equals(entry.name) &&
                    existing.roomNumber.equals(entry.roomNumber) &&
                    existing.day.equals(entry.day) &&
                    existing.timeSlots.equals(entry.timeSlots) &&
                    existing.roomType.equals(entry.roomType)) {
                    entries.set(i, entry);
                    found = true;
                    break;
                }
            }
            if (!found) {
                entries.add(entry);
            }

            try (FileWriter writer = new FileWriter(reservationPath, false)) {
                gson.toJson(entries, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ReservationEntry {
        public String name;
        public String role;
        public String roomType;
        public String roomNumber;
        public String day;
        public List<String> timeSlots;
        public String state;

        public ReservationEntry(String name, String role, String roomType, String roomNumber,
                                String day, List<String> timeSlots, String state) {
            this.name = name;
            this.role = role;
            this.roomType = roomType;
            this.roomNumber = roomNumber;
            this.day = day;
            this.timeSlots = timeSlots;
            this.state = state;
        }

        public String getRole() {
            return role;
        }

        public String getRoomType() {
            return roomType;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public String getDay() {
            return day;
        }

        public List<String> getTimeSlots() {
            return timeSlots;
        }
    }
}