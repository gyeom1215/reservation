/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.ta;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationModel {

    private static final Logger LOGGER = Logger.getLogger(ReservationModel.class.getName());

    // ✅ reservation.json에서 대기 상태만 읽도록 변경
    public List<Reservation> loadReservedReservations() {
        return readFromReservationJson("대기");
    }

    public List<Reservation> readFromReservationJson(String targetState) {
    List<Reservation> list = new ArrayList<>();
    File file = new File("reservations.json");

    try (FileReader reader = new FileReader(file)) {
        JSONArray arr = new JSONArray(new JSONTokener(reader));

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);

            if (!obj.getString("state").equals(targetState)) continue;

            String name = obj.getString("name");
            String role = obj.getString("role");
            String type = obj.getString("roomType");
            Object roomObj = obj.get("roomNumber");
            int roomNumber = (roomObj instanceof Integer)
            ? (int) roomObj
            : Integer.parseInt(roomObj.toString());
            String day = obj.getString("day");

            JSONArray slotArray = obj.getJSONArray("timeSlots");
            List<String> timeSlots = new ArrayList<>();
            for (int j = 0; j < slotArray.length(); j++) {
                timeSlots.add(slotArray.getString(j));
            }

            list.add(new Reservation(name, role, type, roomNumber, day, timeSlots, targetState));
        }
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error loading reservation data: " + e.getMessage(), e);
        System.err.println("예약 데이터 로드 중 오류 발생: " + e.getMessage());
        e.printStackTrace();
    }

    return list;
}

    public List<Reservation> loadApprovedReservations() {
    List<Reservation> list = new ArrayList<>();
    File file = new File("approved_reservations.json");

    try (FileReader reader = new FileReader(file)) {
        JSONArray arr = new JSONArray(new JSONTokener(reader));

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);

            String name = obj.optString("name", "");
            String role = obj.optString("role", "");
            String type = obj.getString("roomType");
            int roomNumber = obj.getInt("roomNumber");
            String day = obj.getString("day");

            JSONArray timeArray = obj.getJSONArray("timeSlots");
            List<String> timeSlots = new ArrayList<>();
            for (int j = 0; j < timeArray.length(); j++) {
                timeSlots.add(timeArray.getString(j));
            }

            String state = obj.optString("state", "승인");

            list.add(new Reservation(name, role, type, roomNumber, day, timeSlots, state));
        }
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error loading reservation data: " + e.getMessage(), e);
        System.err.println("예약 데이터 로드 중 오류 발생: " + e.getMessage());
        e.printStackTrace();
    }

    return list;
}

    public List<Reservation> loadRejectedReservations() {
    List<Reservation> list = new ArrayList<>();
    File file = new File("rejected_reservations.json");

    try (FileReader reader = new FileReader(file)) {
        JSONArray arr = new JSONArray(new JSONTokener(reader));

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);

            String name = obj.optString("name", "");
            String role = obj.optString("role", "");
            String type = obj.getString("roomType");
            int roomNumber = obj.getInt("roomNumber");
            String day = obj.getString("day");

            JSONArray timeArray = obj.getJSONArray("timeSlots");
            List<String> timeSlots = new ArrayList<>();
            for (int j = 0; j < timeArray.length(); j++) {
                timeSlots.add(timeArray.getString(j));
            }

            String state = obj.optString("state", "거절");

            list.add(new Reservation(name, role, type, roomNumber, day, timeSlots, state));
        }
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error loading reservation data: " + e.getMessage(), e);
        System.err.println("예약 데이터 로드 중 오류 발생: " + e.getMessage());
        e.printStackTrace();
    }

    System.out.println("✅ rejected_reservations.json 항목 수: " + list.size());
    return list;
}
    public List<String[]> loadUsers() {
    List<String[]> users = new ArrayList<>();
    File file = new File("users.json");

    try (FileReader reader = new FileReader(file)) {
        JSONArray arr = new JSONArray(new JSONTokener(reader));
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            String[] row = {
                obj.optString("id", ""),
                obj.optString("name", ""),
                obj.optString("department", ""),
                obj.optString("role", "")
            };
            users.add(row);
        }
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error loading reservation data: " + e.getMessage(), e);
        System.err.println("예약 데이터 로드 중 오류 발생: " + e.getMessage());
        e.printStackTrace();
    }

    return users;
}
    public boolean deleteUser(String userId) {
    File file = new File("users.json");

    try (FileReader reader = new FileReader(file)) {
        JSONArray arr = new JSONArray(new JSONTokener(reader));
        JSONArray updated = new JSONArray();

        boolean deleted = false;

        for (int i = 0; i < arr.length(); i++) {
            JSONObject user = arr.getJSONObject(i);
            if (!user.getString("id").equals(userId)) {
                updated.put(user);
            } else {
                deleted = true;
            }
        }

        if (deleted) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(updated.toString(4)); // pretty print
            }
        }

        return deleted;
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error deleting reservation data: " + e.getMessage(), e);
        System.err.println("예약 데이터 삭제 중 오류 발생: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
    }
    public void saveApprovedReservation(Reservation reservation) {
    try {
        File file = new File("approved_reservations.json");
        JSONArray arr = file.exists()
            ? new JSONArray(new JSONTokener(new FileReader(file)))
            : new JSONArray();

        JSONObject obj = new JSONObject();
        obj.put("name", reservation.getName());
        obj.put("role", reservation.getRole());
        obj.put("roomType", reservation.getType());
        obj.put("roomNumber", reservation.getRoomNumber());
        obj.put("day", reservation.getDay());
        obj.put("timeSlots", reservation.getTimeSlots());
        obj.put("state", reservation.getState());

        arr.put(obj);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(arr.toString(4)); // pretty print
        }
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error saving reservation data: " + e.getMessage(), e);
        System.err.println("예약 데이터 저장 중 오류 발생: " + e.getMessage());
        e.printStackTrace();
    }
}

public void saveRejectedReservation(Reservation reservation) {
    try {
        File file = new File("rejected_reservations.json");
        JSONArray arr = file.exists()
            ? new JSONArray(new JSONTokener(new FileReader(file)))
            : new JSONArray();

        JSONObject obj = new JSONObject();
        obj.put("name", reservation.getName());
        obj.put("role", reservation.getRole());
        obj.put("roomType", reservation.getType());
        obj.put("roomNumber", reservation.getRoomNumber());
        obj.put("day", reservation.getDay());
        obj.put("timeSlots", reservation.getTimeSlots());
        obj.put("state", reservation.getState());

        arr.put(obj);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(arr.toString(4));
        }
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error saving reservation data: " + e.getMessage(), e);
        System.err.println("예약 데이터 저장 중 오류 발생: " + e.getMessage());
        e.printStackTrace();
    }
}
}
