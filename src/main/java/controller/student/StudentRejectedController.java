package controller.student;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.student.StudentApprovedListModel;
import model.student.StudentApprovedModel;
import model.user.User;
import view.student.StudentApprovedView;

import model.room.RoomModel;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudentRejectedController {

    public static void openUserReservation(User user) {
        if (!"STUDENT".equals(user.getRole())) {
            System.out.println("학생만 이용 가능합니다.");
            return;
        }

        String jsonFilePath = "reservation.json";

        try {
            StudentApprovedListModel model = new StudentApprovedListModel(jsonFilePath);
            List<StudentApprovedModel> mySchedules = model.getMySchedules(user.getName(), user.getRole());

            SwingUtilities.invokeLater(() -> {
                StudentApprovedView view = new StudentApprovedView(mySchedules);
                view.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean cancelReservation(
            String reservationFile,
            int roomNumber,
            String userName,
            String userRole,
            List<String> timeSlots,
            String day,
            String RoomType
    ) {
        try {
            // StudentReserveController의 ReservationEntry 모델 사용
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
            java.io.File file = new java.io.File(reservationFile);
            java.util.List<StudentReserveController.ReservationEntry> entries = new java.util.ArrayList<>();
            
            if (file.exists() && file.length() > 0) {
                try (java.io.FileReader reader = new java.io.FileReader(file)) {
                    java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<StudentReserveController.ReservationEntry>>() {}.getType();
                    List<StudentReserveController.ReservationEntry> existing = gson.fromJson(reader, listType);
                    if (existing != null) {
                        entries = new java.util.ArrayList<>(existing);
                    }
                }
            }

            boolean success = false;
            for (int i = 0; i < entries.size(); i++) {
                StudentReserveController.ReservationEntry entry = entries.get(i);
                if (entry.roomNumber.equals(String.valueOf(roomNumber))
                        && userName.equals(entry.name)
                        && userRole.equals(entry.getRole())
                        && entry.timeSlots.equals(timeSlots)
                        && day.equals(entry.day)
                        && ("승인".equals(entry.state) || "대기".equals(entry.state))) {
                    entry.state = "취소";
                    success = true;
                    break;
                }
            }

            if (success) {
                // 예약 상태 업데이트 저장
                try (java.io.FileWriter writer = new java.io.FileWriter(reservationFile, false)) {
                    gson.toJson(entries, writer);
                }

                // 강의실 상태 업데이트
                String roomTypeKR = "일반실".equals(RoomType) || "강의실".equals(RoomType) ? "src/normal_room.json"
                        : "실습실".equals(RoomType) ? "src/Lab_room.json"
                        : null;
                if (roomTypeKR == null)
                    throw new IllegalArgumentException("알 수 없는 RoomType 입력: " + RoomType);

                if ("src/Lab_room.json".equals(roomTypeKR)) {
                    updateLabRoomState(roomTypeKR, roomNumber, day, timeSlots);
                } else if ("src/normal_room.json".equals(roomTypeKR)) {
                    updateRoomState(roomTypeKR, roomNumber, day, timeSlots);
                }
            }
            return success;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static void updateLabRoomState(String labRoomJsonPath, int roomNumber, String day, List<String> timeSlots) {
        RoomModel roomModel = new RoomModel(labRoomJsonPath);
        for (String time : timeSlots) {
            roomModel.markCancelled(roomNumber, day, time, labRoomJsonPath);
        }
    }

    private static void updateRoomState(String roomJsonPath, int roomNumber, String day, List<String> timeSlots) {
        RoomModel roomModel = new RoomModel(roomJsonPath);
        for (String time : timeSlots) {
            roomModel.markCancelled(roomNumber, day, time, roomJsonPath);
        }
    }

    public static List<StudentApprovedModel> getMyPendingOrApprovedReservations(
            String reservationFile, String loginUserName, String loginUserRole) throws Exception {
        StudentApprovedListModel allList = new StudentApprovedListModel(reservationFile);
        List<StudentApprovedModel> filtered = new ArrayList<>();
        for (StudentApprovedModel m : allList.getList()) {
            if (loginUserRole.equals(m.getRole()) &&
                    loginUserName.equals(m.getName()) &&
                    ("승인".equals(m.getState()) || "대기".equals(m.getState()))) {
                filtered.add(m);
            }
        }
        return filtered;
    }
}