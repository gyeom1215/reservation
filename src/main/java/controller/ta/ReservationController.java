/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.ta;
import org.json.*;
import model.ta.Reservation;
import model.ta.ReservationModel;
import view.ta.featureFrame;

import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationController {
    private ReservationModel model;
    private featureFrame view;
    private LogController logController;
    private static final Logger LOGGER = Logger.getLogger(ReservationController.class.getName());

    public ReservationController(ReservationModel model, featureFrame view) {
        this.model = model;
        this.view = view;
        this.logController = new LogController();
    }

    public void loadReservedDataToTable() {
        List<Reservation> reservations = model.loadReservedReservations();
        DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"이름", "역할", "강의실 유형", "강의실 번호", "요일", "시간대", "상태"}, 0
        );

        for (Reservation r : reservations) {
            tableModel.addRow(new Object[] {
                r.getName(),
                r.getRole(),
                r.getType(),
                r.getRoomNumber(),
                r.getDay(),
                String.join(", ", r.getTimeSlots()),
                r.getState()
            });
        }

        view.setReservationTableModel(tableModel);
    }

    public void approveSelectedReservation(int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "예약을 선택하세요.");
            return;
        }

        JTable table = view.getReservationTable();
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        String name = (String) tableModel.getValueAt(selectedRow, 0);
        String role = (String) tableModel.getValueAt(selectedRow, 1);
        String type = (String) tableModel.getValueAt(selectedRow, 2);
        int roomNumber = (int) tableModel.getValueAt(selectedRow, 3);
        String day = (String) tableModel.getValueAt(selectedRow, 4);
        String timeSlot = (String) tableModel.getValueAt(selectedRow, 5);

        List<String> timeSlots = new ArrayList<>();
        timeSlots.add(timeSlot);

        Reservation approved = new Reservation(name, role, type, roomNumber, day, timeSlots, "승인");
        boolean success = saveApprovedReservation(approved);

        if (success) {
    updateReservationState(approved, "승인");
    logController.saveTaLog("[대기->승인]", approved);  // ✅ 로그 저장
    tableModel.removeRow(selectedRow);
    JOptionPane.showMessageDialog(view, "예약이 승인되었습니다.");
} else {
            JOptionPane.showMessageDialog(view, "승인 중 오류가 발생했습니다.");
        }
    }

    public void rejectSelectedReservation(int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "예약을 선택하세요.");
            return;
        }

        JTable table = view.getReservationTable();
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        String name = (String) tableModel.getValueAt(selectedRow, 0);
        String role = (String) tableModel.getValueAt(selectedRow, 1);
        String type = (String) tableModel.getValueAt(selectedRow, 2);
        int roomNumber = (int) tableModel.getValueAt(selectedRow, 3);
        String day = (String) tableModel.getValueAt(selectedRow, 4);
        String timeSlot = (String) tableModel.getValueAt(selectedRow, 5);

        List<String> timeSlots = new ArrayList<>();
        timeSlots.add(timeSlot);

        Reservation rejected = new Reservation(name, role, type, roomNumber, day, timeSlots, "거절");
        boolean success = saveRejectedReservation(rejected);

        if (success) {
        updateReservationState(rejected, "거절");
        logController.saveTaLog("[대기->거절]", rejected);  // ✅ 로그 저장
        tableModel.removeRow(selectedRow);
        JOptionPane.showMessageDialog(view, "예약이 거절되었습니다.");
        } else {
            JOptionPane.showMessageDialog(view, "거절 중 오류가 발생했습니다.");
        }
    }

    private boolean saveApprovedReservation(Reservation reservation) {
    JSONArray data = new JSONArray();
    File file = new File("approved_reservations.json");

    // 기존 데이터 배열 불러오기
    try (FileReader reader = new FileReader(file)) {
        data = new JSONArray(new JSONTokener(reader));
    } catch (Exception e) {
        // 파일이 없을 경우 처음 생성 → 무시
    }

    // 새 예약 정보 추가
    JSONObject obj = new JSONObject();
    obj.put("name", reservation.getName());
    obj.put("role", reservation.getRole());
    obj.put("roomType", reservation.getType());
    obj.put("roomNumber", reservation.getRoomNumber());
    obj.put("day", reservation.getDay());
    obj.put("timeSlots", reservation.getTimeSlots());
    obj.put("state", reservation.getState());
    data.put(obj);

    // 파일에 전체 JSONArray 저장
    try (FileWriter writer = new FileWriter(file)) {
        writer.write(data.toString(2)); // 2는 pretty print indent
        return true;
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error saving approved reservation: " + e.getMessage(), e);
        System.err.println("예약 저장 중 오류 발생: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

    private boolean saveRejectedReservation(Reservation reservation) {
    JSONArray data = new JSONArray();
    File file = new File("rejected_reservations.json");

    // 기존 데이터 읽어오기 (없으면 새로 생성)
    try (FileReader reader = new FileReader(file)) {
        data = new JSONArray(new JSONTokener(reader));
    } catch (Exception e) {
        // 파일 없으면 무시 (처음 생성 시)
    }

    // 새 예약 정보 JSON 객체로 변환
    JSONObject obj = new JSONObject();
    obj.put("name", reservation.getName());
    obj.put("role", reservation.getRole());
    obj.put("roomType", reservation.getType());
    obj.put("roomNumber", reservation.getRoomNumber());
    obj.put("day", reservation.getDay());
    obj.put("timeSlots", reservation.getTimeSlots());
    obj.put("state", reservation.getState());

    data.put(obj); // 배열에 추가

    // 파일에 저장 (덮어쓰기)
    try (FileWriter writer = new FileWriter(file)) {
        writer.write(data.toString(2)); // 예쁘게 출력
        return true;
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error saving rejected reservation: " + e.getMessage(), e);
        System.err.println("예약 저장 중 오류 발생: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

    public void updateReservationState(Reservation reservation, String newState) {
    File file = new File("reservations.json");

    try (FileReader reader = new FileReader(file)) {
        JSONArray arr = new JSONArray(new JSONTokener(reader));

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);

            if (
                obj.getInt("roomNumber") == reservation.getRoomNumber()
                && obj.getString("day").equals(reservation.getDay())
                && obj.getJSONArray("timeSlots").toString().equals(new JSONArray(reservation.getTimeSlots()).toString())
            ) {
                obj.put("state", newState);
                System.out.println("✅ 상태 변경됨 → " + newState);
                break;
            }
        }

        // 다시 파일에 전체 저장
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(arr.toString(2)); // pretty print
        }

    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error updating reservation state: " + e.getMessage(), e);
        System.err.println("예약 상태 변경 중 오류 발생: " + e.getMessage());
        e.printStackTrace();
    }
}
}

