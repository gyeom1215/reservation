package com.deu.reservation.client.service;

import com.deu.reservation.client.model.Reservation;
import com.deu.reservation.client.model.Room;
import com.deu.reservation.client.model.User;
import com.deu.reservation.client.model.ReservationStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ReservationService {
    private static final String SERVER_URL = "http://localhost:8080/api"; // 서버 URL 설정
    private static final String RESERVATIONS_FILE = "reservations.json";
    private static final String ROOMS_FILE = "rooms.json";
    private static final LocalTime START_TIME = LocalTime.of(9, 0);  // 09:00
    private static final LocalTime END_TIME = LocalTime.of(18, 0);   // 18:00
    private List<Reservation> reservations;
    private List<Room> rooms;
    private final Gson gson;

    public ReservationService() {
        this.reservations = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
        loadReservations();
        initializeRooms();
    }

    private void loadReservations() {
        try (FileReader reader = new FileReader(RESERVATIONS_FILE)) {
            reservations = gson.fromJson(reader, new TypeToken<List<Reservation>>(){}.getType());
            if (reservations == null) {
                reservations = new ArrayList<>();
            }
        } catch (IOException e) {
            System.err.println("예약 데이터를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            reservations = new ArrayList<>();
        }
    }

    private void saveReservations() {
        try (FileWriter writer = new FileWriter(RESERVATIONS_FILE)) {
            gson.toJson(reservations, writer);
        } catch (IOException e) {
            System.err.println("예약 데이터를 저장하는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void initializeRooms() {
        // 강의실
        rooms.add(new Room("908", "강의실", "908호"));
        rooms.add(new Room("912", "강의실", "912호"));
        rooms.add(new Room("913", "강의실", "913호"));
        rooms.add(new Room("914", "강의실", "914호"));
        // 실습실
        rooms.add(new Room("911", "실습실", "911호"));
        rooms.add(new Room("915", "실습실", "915호"));
        rooms.add(new Room("916", "실습실", "916호"));
        rooms.add(new Room("918", "실습실", "918호"));
    }

    public List<Room> getRooms() {
        return new ArrayList<>(rooms);
    }

    public List<Room> getRoomsByType(String type) {
        if (type == null) {
            return new ArrayList<>();
        }
        return rooms.stream()
            .filter(room -> type.equals(room.getType()))
            .collect(Collectors.toList());
    }

    public List<Reservation> getUserReservations(String userId) {
        try {
            URL url = new URL(SERVER_URL + "/reservations/user/" + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();

            return gson.fromJson(response.toString(), new TypeToken<List<Reservation>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Reservation> getPendingReservations() {
        try {
            URL url = new URL(SERVER_URL + "/reservations/pending");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();

            return gson.fromJson(response.toString(), new TypeToken<List<Reservation>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean createReservation(String userId, String roomId, LocalDateTime startTime, 
            LocalDateTime endTime, String purpose, User user) {
        try {
            URL url = new URL(SERVER_URL + "/reservations");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ReservationDto dto = new ReservationDto(userId, roomId, startTime, endTime, purpose);
            String jsonInputString = gson.toJson(dto);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();

            return gson.fromJson(response.toString(), Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean approveReservation(String reservationId, User approver) {
        try {
            URL url = new URL(SERVER_URL + "/reservations/" + reservationId + "/approve");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = gson.toJson(approver);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();

            return gson.fromJson(response.toString(), Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rejectReservation(String reservationId, User rejector) {
        try {
            URL url = new URL(SERVER_URL + "/reservations/" + reservationId + "/reject");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = gson.toJson(rejector);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();

            return gson.fromJson(response.toString(), Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidReservationTime(LocalDateTime startTime, LocalDateTime endTime) {
        // 요일 체크 (월~금)
        DayOfWeek dayOfWeek = startTime.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        // 시간 체크 (09:00 ~ 18:00)
        LocalTime startTimeOfDay = startTime.toLocalTime();
        LocalTime endTimeOfDay = endTime.toLocalTime();
        
        return !startTimeOfDay.isBefore(START_TIME) && 
               !endTimeOfDay.isAfter(END_TIME) &&
               startTimeOfDay.isBefore(endTimeOfDay);
    }

    public boolean isRoomAvailable(String roomId, LocalDateTime startTime, LocalDateTime endTime) {
        if (!isValidReservationTime(startTime, endTime)) {
            return false;
        }

        return reservations.stream()
            .filter(r -> r.getRoomId().equals(roomId))
            .filter(r -> r.getStatus() == ReservationStatus.APPROVED)
            .noneMatch(r -> 
                (startTime.isBefore(r.getEndTime()) && endTime.isAfter(r.getStartTime()))
            );
    }

    public boolean cancelReservation(String reservationId, User user) {
        try {
            URL url = new URL(SERVER_URL + "/reservations/" + reservationId + "/cancel");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = gson.toJson(user);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();

            return gson.fromJson(response.toString(), Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

class LocalDateTimeAdapter implements com.google.gson.JsonSerializer<LocalDateTime>, com.google.gson.JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public com.google.gson.JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
        return new com.google.gson.JsonPrimitive(formatter.format(src));
    }

    @Override
    public LocalDateTime deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
        return LocalDateTime.parse(json.getAsString(), formatter);
    }
}

// DTO 클래스 추가
class ReservationDto {
    private String userId;
    private String roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;

    public ReservationDto(String userId, String roomId, LocalDateTime startTime, LocalDateTime endTime, String purpose) {
        this.userId = userId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
} 