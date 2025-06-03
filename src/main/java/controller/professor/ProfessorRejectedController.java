package controller.professor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.professor.ProfessorApprovedListModel;
import model.professor.ProfessorApprovedModel;
import model.user.User;
import view.professor.ProfessorApprovedView;

// import 문 추가
import model.room.RoomModel;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfessorRejectedController {

    public static void openUserReservation(User user) {
        if (!"PROFESSOR".equals(user.getRole())) {
            System.out.println("교수만 이용 가능합니다.");
            return;
        }

        String jsonFilePath = "reservation.json";

        try {
            ProfessorApprovedListModel model = new ProfessorApprovedListModel(jsonFilePath);
            List<ProfessorApprovedModel> mySchedules = model.getMySchedules(user.getName(), user.getRole());

            SwingUtilities.invokeLater(() -> {
                ProfessorApprovedView view = new ProfessorApprovedView(mySchedules);
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
            String RoomType // 실습실 파일
    ) {
        try {
            ProfessorApprovedListModel allList = new ProfessorApprovedListModel(reservationFile);
            boolean success = false;
            for (ProfessorApprovedModel m : allList.getList()) {
                if (m.getRoomNumber() == roomNumber
                        && userName.equals(m.getName())
                        && userRole.equals(m.getRole())
                        && m.getTimeSlots().equals(timeSlots)
                        && day.equals(m.getDay())
                        && ("승인".equals(m.getState()) || "대기".equals(m.getState()))) {
                    m.setState("취소");
                    success = true;
                    break;
                }
            }
            if (success) {
                allList.save(reservationFile);

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
        RoomModel roomModel = new RoomModel(labRoomJsonPath); // 실습실 json 파일 경로
        for (String time : timeSlots) {
            roomModel.markCancelled(roomNumber, day, time, labRoomJsonPath);
        }
    }

    private static void updateRoomState(String roomJsonPath, int roomNumber, String day, List<String> timeSlots) {
        RoomModel roomModel = new RoomModel(roomJsonPath); // 일반실 json 파일 경로
        for (String time : timeSlots) {
            roomModel.markCancelled(roomNumber, day, time, roomJsonPath);
        }
    }

    public static List<ProfessorApprovedModel> getMyPendingOrApprovedReservations(
            String reservationFile, String loginUserName, String loginUserRole) throws Exception {
        ProfessorApprovedListModel allList = new ProfessorApprovedListModel(reservationFile);
        List<ProfessorApprovedModel> filtered = new ArrayList<>();
        for (ProfessorApprovedModel m : allList.getList()) {
            if (loginUserRole.equals(m.getRole()) &&
                    loginUserName.equals(m.getName()) &&
                    ("승인".equals(m.getState()) || "대기".equals(m.getState()))) {
                filtered.add(m);
            }
        }
        return filtered;
    }
}