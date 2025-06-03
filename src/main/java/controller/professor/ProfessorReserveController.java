package controller.professor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.room.RoomReservation;
import model.room.RoomModel;
import model.common.Reservation;
import model.user.User;
import view.login.LoginView;
import view.professor.ProfessorView;
import client.ReservationClient;

import javax.swing.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ProfessorReserveController {
    private final ProfessorView professorView;
    private final User user;
    private final ReservationClient reservationClient;

    public ProfessorReserveController(ProfessorView professorView, User user) {
        this.professorView = professorView;
        this.user = user;
        this.reservationClient = professorView.getReservationClient();

        professorView.setReservationHandler(this::reserveRoom);
        initListeners();
    }

    public void reserveRoom(Reservation reservation) {
        String jsonPath = reservation.getRoomType().equals("실습실") ? "src/Lab_room.json" : "src/normal_room.json";
        RoomModel roomModel = professorView.getRoomModel();

        // 하나라도 예약 불가인 시간대가 있으면 예약 중단
        for (String timeSlot : reservation.getTimeSlots()) {
            if (!roomModel.isReservable(reservation.getRoomNumber(), reservation.getDay(), timeSlot)) {
                JOptionPane.showMessageDialog(null, "이미 예약된 시간대를 포함하고 있습니다.\n예약할 수 없습니다.");
                return;
            }
        }

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
                "승인"
        ));

        JOptionPane.showMessageDialog(null, "예약이 완료되었습니다.");
    }

    private void initListeners() {
        professorView.getBtnStartReservation().addActionListener(e -> {
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
                String jsonPath = (choice == 0) ? "src/Lab_room.json" : "src/normal_room.json";
                String roomType = options[choice];
                startReservation(jsonPath, roomType);
            }
        });

        professorView.getBtnLogout().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "로그아웃하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                professorView.dispose(); // 현재 창 닫기
                new LoginView(reservationClient);         // 로그인 창 열기
            }
        });
    }

    public List<ReservationEntry> loadActiveReservations(String filePath) {
        Gson gson = new Gson();
        List<ReservationEntry> result = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath)) {
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<ReservationEntry>>() {}.getType();
            // 배열 시도
            List<ReservationEntry> all = gson.fromJson(reader, listType);
            if (all == null) {
                // 파일을 새로 열어서 단일 객체로 파싱
                try (FileReader singleReader = new FileReader(filePath)) {
                    ReservationEntry single = gson.fromJson(singleReader, ReservationEntry.class);
                    if (single != null) {
                        all = new ArrayList<>();
                        all.add(single);
                    }
                }
            }
            if (all != null) {
                result.addAll(all);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void startReservation(String jsonPath, String roomType) {
        List<ReservationEntry> activeLabs = loadActiveReservations("src/Lab_room.json");
        List<ReservationEntry> activeNormals = loadActiveReservations("src/normal_room.json");
        professorView.showReservationUI(jsonPath, roomType);
    }

    public void saveReservationEntry(ReservationEntry entry) {
        saveReservationEntry(entry, "reservations.json");
    }

    public void saveReservationEntry(ReservationEntry entry, String filePath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            java.io.File file = new java.io.File(filePath);
            java.util.List<ReservationEntry> entries = new java.util.ArrayList<>();
            if (file.exists() && file.length() > 0) {
                try (java.io.FileReader reader = new java.io.FileReader(file)) {
                    ReservationEntry[] array = gson.fromJson(reader, ReservationEntry[].class);
                    if (array != null) {
                        entries = new java.util.ArrayList<>(java.util.Arrays.asList(array));
                    }
                }
            }

            entries.add(entry);

            try (FileWriter writer = new FileWriter(filePath, false)) {
                gson.toJson(entries, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ReservationEntry {
        public String name;
        private String role;
        private String roomType;
        private String roomNumber;
        private String day;
        private List<String> timeSlots;
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
    }
}